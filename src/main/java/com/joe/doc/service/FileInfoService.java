package com.joe.doc.service;

import com.joe.doc.config.FileParserComponent;
import com.joe.doc.model.FileInfo;
import com.joe.doc.model.TikaModel;
import com.joe.doc.repository.BaseRepository;
import com.joe.doc.repository.FileInfoRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author JoezBlackZ
 * @description sys user service
 * @date 2020/1/4 10:08
 */
@Slf4j
@Service
public class FileInfoService extends BaseService<FileInfo> {

    @Resource
    private FileInfoRepository fileInfoRepository;

    @Resource
    private FileParserComponent fileParserComponent;

    @Override
    public BaseRepository<FileInfo> getRepository() {
        return this.fileInfoRepository;
    }

    public void upload(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            log.info("Filename: {}", originalFilename);
            TikaModel tikaModel = this.fileParserComponent.parse(file);
            FileInfo fileInfo = FileInfo.builder()
                    .filename(originalFilename)
                    .length(file.getSize())
                    .contentType(file.getContentType())
                    .metadata(tikaModel.getMetadataAsMap())
                    .build();
            this.fileInfoRepository.insert(fileInfo);
        }

    }
}