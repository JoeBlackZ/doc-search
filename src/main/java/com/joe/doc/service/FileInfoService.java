package com.joe.doc.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.joe.doc.common.ResponseResult;
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

    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public BaseRepository<FileInfo> getRepository() {
        return this.fileInfoRepository;
    }

    public ResponseResult upload(List<MultipartFile> multipartFiles) {
        List<String> fileInfoIds = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            if (multipartFile.isEmpty()) {
                log.error("File {} is empty, skip it.", originalFilename);
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
                throw new RuntimeException("文件[" + originalFilename + "]解析失败.", e);
            }
        }
        log.info("上传文件数量: {}, 提交文件数量: {}", multipartFiles.size(), fileInfoIds.size());
        return ResponseResult.success().data(fileInfoIds).msg("文件上传完成，文件信息保存成功，异步解析任务提交成功。");
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
    public ResponseResult removeByIds(Object[] ids) {
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
        ResponseResult responseResult = super.removeByIds(ids);
        if (responseResult.ok()) {
            log.info("{} 个文件删除成功，传入的文件ID: {}", ids.length, Arrays.toString(ids));
        }
        return responseResult;
    }

    public ResponseResult search(String keywords) {
        Query query = new Query.Builder().queryString(q -> q.query(keywords)).build();
        SearchRequest searchRequest = new SearchRequest.Builder().index(FILE_INFO_INDEX_NAME).query(query).build();
        try {
            SearchResponse<FileInfoIndexModel> searchResponse = this.elasticsearchClient.search(searchRequest, FileInfoIndexModel.class);
            HitsMetadata<FileInfoIndexModel> hitsMetadata = searchResponse.hits();
            List<Hit<FileInfoIndexModel>> hits = hitsMetadata.hits();
            List<FileInfoIndexModel> searchResults = new ArrayList<>();
            for (Hit<FileInfoIndexModel> hit : hits) {
                FileInfoIndexModel infoIndexModel = hit.source();
                searchResults.add(infoIndexModel);
            }
            TotalHits totalHits = hitsMetadata.total();
            FileInfoSearchResult searchResult = FileInfoSearchResult.builder()
                    .took(searchResponse.took())
                    .hitCount(Objects.isNull(totalHits) ? 0 : totalHits.value())
                    .fileInfos(searchResults)
                    .build();
            return ResponseResult.success().data(searchResult);
        } catch (IOException e) {
            log.error("搜过关键字[{}]异常.", keywords, e);
            return ResponseResult.fail().msg("关键字搜索异常。");
        }
    }
}
