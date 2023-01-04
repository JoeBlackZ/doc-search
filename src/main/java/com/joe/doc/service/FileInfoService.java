package com.joe.doc.service;

import com.joe.doc.config.FileParserComponent;
import com.joe.doc.model.FileInfo;
import com.joe.doc.repository.BaseRepository;
import com.joe.doc.repository.FileInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @description sys user service
 * @author JoezBlackZ
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
            FileInfo fileInfo = FileInfo.builder()
                    .filename(originalFilename)
                    .length(file.getSize())
                    .contentType(file.getContentType())
                    .build();
            this.fileInfoRepository.insert(fileInfo);
        }

    }
}
