package com.joe.doc.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.joe.doc.common.ResponseResult;
import com.joe.doc.model.FileInfo;
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
            log.info("Filename: {}", originalFilename);
            try (BufferedInputStream buffer = new BufferedInputStream(multipartFile.getInputStream())) {
                // 保存文件
                String fileInfoId = this.gridFsRepository.store(buffer, originalFilename);
                FileInfo fileInfo = FileInfo.builder()
                        .filename(originalFilename)
                        .length(multipartFile.getSize())
                        .contentType(multipartFile.getContentType())
                        .build();
                fileInfo.setId(fileInfoId);
                fileInfo.setCreateDate(new Date());
                // 保存文件信息
                this.fileInfoRepository.insert(fileInfo);
                // 提交解析文件任务
                this.submitFileParseTask(fileInfoId);
                fileInfoIds.add(fileInfoId);
            } catch (IOException e) {
                throw new RuntimeException("文件[" + originalFilename + "]解析失败.", e);
            }
        }
        log.info("File count: {}, FileInfo count: {}", multipartFiles.size(), fileInfoIds.size());
        return ResponseResult.success().data(fileInfoIds).msg("文件上传完成，文件信息保存成功，异步解析任务提交成功。");
    }

    private void submitFileParseTask(String fileInfoId) {
        GridFsResource resource = this.gridFsRepository.getGridFsFileResource(fileInfoId);
        this.fileParserService.submitFileParseTask(() -> {
            try (BufferedInputStream buffer = new BufferedInputStream(resource.getInputStream())) {
                TikaModel tikaModel = fileParserService.parse(buffer);
                Map<String, Object> metadataAsMap = tikaModel.getMetadataAsMap();
                FileParseInfo fileParseInfo = dealFileParseInfo(metadataAsMap);
                fileParseInfo.setId(fileInfoId);
                fileParseInfoRepository.insert(fileParseInfo);
            } catch (IOException e) {
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
        return super.removeByIds(ids);
    }

    private FileParseInfo dealFileParseInfo(Map<String, Object> metadataAsMap) {
        FileParseInfo fileParseInfo = FileParseInfo.builder()
                .metadata(metadataAsMap)
                .build();
        Object dcCreator = metadataAsMap.get("dc:creator");
        if (Objects.nonNull(dcCreator)) {
            fileParseInfo.setCreateUserId(dcCreator.toString());
        }
        Object dcTermsCreated = metadataAsMap.get("dcterms:created");
        if (Objects.nonNull(dcTermsCreated)) {
            DateTime dateTime = DateUtil.parseUTC(dcTermsCreated.toString());
            fileParseInfo.setCreateDate(dateTime.toJdkDate());
        }
        return fileParseInfo;
    }
}
