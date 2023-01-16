package com.joe.doc.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.joe.doc.common.ResponseResult;
import com.joe.doc.model.FileInfo;
import com.joe.doc.model.FileInfoIndexModel;
import com.joe.doc.model.FileParseInfo;
import com.joe.doc.model.TikaModel;
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
        String fileInfoId = fileInfo.getId();
        GridFsResource resource = this.gridFsRepository.getGridFsFileResource(fileInfoId);
        this.fileParserService.submitFileParseTask(() -> {
            try (BufferedInputStream buffer = new BufferedInputStream(resource.getInputStream())) {
                TikaModel tikaModel = fileParserService.parse(buffer);
                Map<String, Object> metadataAsMap = tikaModel.getMetadataAsMap();
                FileParseInfo fileParseInfo = dealFileParseInfo(metadataAsMap);
                fileParseInfo.setId(fileInfoId);
                FileParseInfo insert = fileParseInfoRepository.insert(fileParseInfo);
                if (Objects.nonNull(insert.getId())) {
                    log.info("文件 {} 解析完成.", fileInfoId);
                }
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

    @Override
    public ResponseResult removeByIds(Object[] ids) {
        // 删除文件
        this.gridFsRepository.remove(ids);
        // 删除文件解析的信息
        this.fileParseInfoRepository.deleteByIds(ids);
        // 删除文件信息
        ResponseResult responseResult = super.removeByIds(ids);
        if (responseResult.ok()) {
            log.info("文件删除成功，文件ID: {}", Arrays.toString(ids));
        }
        return responseResult;
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
}
