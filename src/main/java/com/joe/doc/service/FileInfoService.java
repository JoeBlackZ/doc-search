package com.joe.doc.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import com.joe.doc.common.ResponseResult;
import com.joe.doc.constant.SearchScope;
import com.joe.doc.constant.SearchType;
import com.joe.doc.model.*;
import com.joe.doc.repository.BaseRepository;
import com.joe.doc.repository.FileInfoRepository;
import com.joe.doc.repository.FileParseInfoRepository;
import com.joe.doc.repository.GridFsRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.*;


/**
 * @author JoezBlackZ
 * @description FileInfoService
 * @date 2020/1/4 10:08
 */
@Slf4j
@Service
public class FileInfoService extends BaseService<FileInfo> {

    @Resource
    private FileInfoRepository fileInfoRepository;

    @Resource
    private FileParseInfoRepository fileParseInfoRepository;

    @Resource
    private FileParserService fileParserService;

    @Resource
    private GridFsRepository gridFsRepository;

    private static final String FILE_INFO_INDEX_NAME = "file_info";
    private static final String SEARCH_FIELD_FILENAME = "filename";
    private static final String SEARCH_FIELD_CONTENT = "content";

    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public BaseRepository<FileInfo> getRepository() {
        return this.fileInfoRepository;
    }

    public ResponseResult<List<String>> upload(List<MultipartFile> multipartFiles) {
        List<String> fileInfoIds = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            if (multipartFile.isEmpty()) {
                log.error("文件 [{}] 为空, 已跳过.", originalFilename);
                continue;
            }
            try (BufferedInputStream buffer = new BufferedInputStream(multipartFile.getInputStream())) {
                // 保存文件
                String fileInfoId = this.gridFsRepository.store(buffer, originalFilename);
                FileInfo fileInfo = FileInfo.builder()
                        .filename(originalFilename)
                        .length(multipartFile.getSize())
                        .contentType(multipartFile.getContentType())
                        .fileExtName(FileUtil.extName(originalFilename))
                        .build();
                fileInfo.setId(fileInfoId);
                fileInfo.setCreateDate(new Date());
                // 保存文件信息
                FileInfo insert = this.fileInfoRepository.insert(fileInfo);
                if (Objects.nonNull(insert.getId())) {
                    log.info("文件 {}({}) 上传并保存成功.", fileInfoId, originalFilename);
                }
                // 提交解析文件任务
                this.submitFileParseTask(insert);
                // 提交计算文件MD5的任务
                this.submitCalcFileMd5Task(fileInfoId);
                fileInfoIds.add(fileInfoId);
            } catch (IOException e) {
                throw new RuntimeException("文件 [" + originalFilename + "] 解析失败.", e);
            }
        }
        log.info("上传文件数量: {}, 提交文件数量: {}", multipartFiles.size(), fileInfoIds.size());
        return ResponseResult.success(fileInfoIds, "文件上传完成，文件信息保存成功，异步解析任务提交成功。");
    }

    private void submitFileParseTask(FileInfo fileInfo) {
        // 获取文件资源
        String fileInfoId = fileInfo.getId();
        GridFsResource resource = this.gridFsRepository.getGridFsFileResource(fileInfoId);
        // 提交解析任务
        this.fileParserService.submitFileParseTask(() -> {
            try (BufferedInputStream buffer = new BufferedInputStream(resource.getInputStream())) {
                // 解析文件
                TikaModel tikaModel = fileParserService.parse(buffer);
                Map<String, Object> metadataAsMap = tikaModel.getMetadataAsMap();
                // 从文件元数据中获取相关的文件信息
                FileParseInfo fileParseInfo = dealFileParseInfo(metadataAsMap);
                fileParseInfo.setId(fileInfoId);
                // 保存文件解析信息
                FileParseInfo insert = fileParseInfoRepository.insert(fileParseInfo);
                if (Objects.nonNull(insert.getId())) {
                    log.info("文件 {} 解析完成.", fileInfoId);
                }
                // 要索引到ES的数据
                FileInfoIndexModel fileInfoIndexModel = FileInfoIndexModel.builder()
                        .filename(fileInfo.getFilename())
                        .contentType(fileInfo.getContentType())
                        .fileLength(fileInfo.getLength())
                        .fileExtName(fileInfo.getFileExtName())
                        .content(tikaModel.getContent())
                        .build();
                IndexRequest<FileInfoIndexModel> request = IndexRequest.of(i -> i.index(FILE_INFO_INDEX_NAME)
                        .id(fileInfoId).document(fileInfoIndexModel));
                IndexResponse indexResponse = elasticsearchClient.index(request);
                log.info("文件信息已索引到ES, id: {}, version: {}", indexResponse.id(), indexResponse.version());
            } catch (Exception e) {
                log.error("文件解析异常.", e);
            }
        });
    }

    private void submitCalcFileMd5Task(String fileInfoId) {
        GridFsResource resource = this.gridFsRepository.getGridFsFileResource(fileInfoId);
        this.fileParserService.submitFileParseTask(() -> {
            try (BufferedInputStream buffer = new BufferedInputStream(resource.getInputStream())) {
                String md5Hex = DigestUtil.md5Hex(buffer);
                FileParseInfo fileParseInfo = FileParseInfo.builder().md5(md5Hex).build();
                fileParseInfo.setId(fileInfoId);
                fileParseInfoRepository.updateById(fileParseInfo);
            } catch (Exception e) {
                log.error("文件解析异常.", e);
            }
        });
    }

    private FileParseInfo dealFileParseInfo(Map<String, Object> metadataAsMap) {
        FileParseInfo fileParseInfo = FileParseInfo.builder().metadata(metadataAsMap).build();
        Object dcCreator = metadataAsMap.get("dc:creator");
        if (Objects.nonNull(dcCreator)) {
            fileParseInfo.setCreateUserId(dcCreator.toString());
        }
        Object dcTermsCreated = metadataAsMap.get("dcterms:created");
        if (Objects.nonNull(dcTermsCreated)) {
            DateTime dateTime = DateUtil.parseUTC(dcTermsCreated.toString());
            fileParseInfo.setCreateDate(dateTime.toJdkDate());
        }

        Object metaLastAuthor = metadataAsMap.get("meta:last-author");
        if (Objects.nonNull(metaLastAuthor)) {
            fileParseInfo.setUpdateUserId(metaLastAuthor.toString());
        }
        Object dcTermsModified = metadataAsMap.get("dcterms:modified");
        if (Objects.nonNull(dcTermsModified)) {
            DateTime dateTime = DateUtil.parseUTC(dcTermsModified.toString());
            fileParseInfo.setUpdateDate(dateTime.toJdkDate());
        }
        return fileParseInfo;
    }

    @Override
    public ResponseResult<Long> removeByIds(Object[] ids) {
        // 删除文件
        this.gridFsRepository.remove(ids);
        // 删除文件解析的信息
        this.fileParseInfoRepository.deleteByIds(ids);
        // 删除 Elasticsearch 中索引的文件信息
        List<String> idList = Arrays.stream(ids).map(Object::toString).toList();
        DeleteByQueryRequest queryRequest = new DeleteByQueryRequest.Builder()
                .index(FILE_INFO_INDEX_NAME)
                .query(query -> query.ids(idsQuery -> idsQuery.values(idList)))
                .build();
        try {
            DeleteByQueryResponse queryResponse = this.elasticsearchClient.deleteByQuery(queryRequest);
            Long deleted = queryResponse.deleted();
            log.info("{} 个文件信息从 Elasticsearch 中删除.", deleted);
        } catch (IOException e) {
            log.error("删除 Elasticsearch 中索引的文件信息失败，文件ID: {}", ids);
        }
        // 删除文件信息
        ResponseResult<Long> responseResult = super.removeByIds(ids);
        if (responseResult.ok()) {
            log.info("{} 个文件删除成功，传入的文件ID: {}", ids.length, Arrays.toString(ids));
        }
        return responseResult;
    }

    public ResponseResult<FileInfoSearchResult> search(FileInfoSearchParam searchParam) {
        Map<String, HighlightField> fieldMap = this.getHighlightField(searchParam.getSearchScope());
        Highlight highlight = new Highlight.Builder().fields(fieldMap)
                .numberOfFragments(3)
                .fragmentSize(150)
                .noMatchSize(100)
                .order(HighlighterOrder.Score)
                .preTags("<tag>").postTags("</tag>")
                .build();
        Query query = this.getQuery(searchParam);
        SearchRequest searchRequest = new SearchRequest.Builder().index(FILE_INFO_INDEX_NAME)
                .query(query).highlight(highlight).build();
        try {
            SearchResponse<FileInfoSearchResult.Item> searchResponse = this.elasticsearchClient.search(searchRequest,
                    FileInfoSearchResult.Item.class);
            HitsMetadata<FileInfoSearchResult.Item> hitsMetadata = searchResponse.hits();
            List<Hit<FileInfoSearchResult.Item>> hits = hitsMetadata.hits();
            List<FileInfoIndexModel> searchResults = new ArrayList<>();
            for (Hit<FileInfoSearchResult.Item> hit : hits) {
                FileInfoSearchResult.Item item = hit.source();
                if (Objects.isNull(item)) {
                    continue;
                }
                Map<String, List<String>> highlightMap = hit.highlight();
                List<String> filenameHighlight = highlightMap.get(SEARCH_FIELD_FILENAME);
                if (Objects.nonNull(filenameHighlight)) {
                    item.setFilename(String.join("", filenameHighlight));
                }

                List<String> contentHighlight = highlightMap.get(SEARCH_FIELD_CONTENT);
                if (Objects.nonNull(contentHighlight)) {
                    item.setContent(String.join("", contentHighlight));
                }
                item.setScope(hit.score());
                item.setId(hit.id());
                searchResults.add(item);
            }
            TotalHits totalHits = hitsMetadata.total();
            FileInfoSearchResult searchResult = FileInfoSearchResult.builder().took(searchResponse.took())
                    .hitCount(Objects.isNull(totalHits) ? 0 : totalHits.value())
                    .fileInfos(searchResults).build();
            return ResponseResult.success(searchResult, "搜索成功");
        } catch (IOException e) {
            log.error("搜过关键字[{}]异常.", searchParam.getKeywords(), e);
            return ResponseResult.fail("关键字搜索异常。");
        }
    }

    private Map<String, HighlightField> getHighlightField(String searchScope) {
        Map<String, HighlightField> fieldMap = new HashMap<>();
        HighlightField filenameHighlightField = new HighlightField.Builder().matchedFields(SEARCH_FIELD_FILENAME).build();
        HighlightField contentHighlightField = new HighlightField.Builder().matchedFields(SEARCH_FIELD_CONTENT).build();
        if (Objects.equals(SearchScope.FILENAME.getScope(), searchScope)) {
            fieldMap.put(SEARCH_FIELD_FILENAME, filenameHighlightField);
        } else if (Objects.equals(SearchScope.FILE_CONTENT.getScope(), searchScope)) {
            fieldMap.put(SEARCH_FIELD_CONTENT, contentHighlightField);
        } else {
            fieldMap.put(SEARCH_FIELD_FILENAME, filenameHighlightField);
            fieldMap.put(SEARCH_FIELD_CONTENT, contentHighlightField);
        }
        return fieldMap;
    }

    private Query getQuery(FileInfoSearchParam searchParam) {
        BoolQuery.Builder builder = new BoolQuery.Builder();
        String keywords = searchParam.getKeywords();
        String searchType = searchParam.getSearchType();
        String searchScope = searchParam.getSearchScope();
        List<String> queryField = this.getQueryField(searchScope);
        if (StrUtil.isBlank(searchType) || Objects.equals(SearchType.SIMPLE_QUERY_STRING.getTypeName(), searchType)) {
            SimpleQueryStringQuery simpleQueryStringQuery = new SimpleQueryStringQuery.Builder()
                    .fields(queryField).query(keywords).build();
            Query query = new Query.Builder().simpleQueryString(simpleQueryStringQuery).build();
            builder.must(query);
        } else if (Objects.equals(SearchType.MATCH_PHRASE.getTypeName(), searchType)) {
            for (String field : queryField) {
                MatchPhraseQuery matchPhraseQuery = new MatchPhraseQuery.Builder().field(field).query(keywords).build();
                Query query = new Query.Builder().matchPhrase(matchPhraseQuery).build();
                builder.should(query);
            }
        }
        return new Query(builder.build());
    }

    private List<String> getQueryField(String searchScope) {
        if (Objects.equals(searchScope, SearchScope.FILENAME.getScope())) {
            return List.of(SearchScope.FILENAME.getScope());
        } else if (Objects.equals(searchScope, SearchScope.FILE_CONTENT.getScope())) {
            return List.of(SearchScope.FILE_CONTENT.getScope());
        } else {
            return Arrays.stream(SearchScope.values()).map(SearchScope::getScope).toList();
        }
    }
}
