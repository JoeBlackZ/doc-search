package com.joe.doc.service;

import com.joe.doc.common.ResponseResult;
import com.joe.doc.config.FileParserComponent;
import com.joe.doc.model.FileInfo;
import com.joe.doc.model.TikaModel;
import com.joe.doc.repository.BaseRepository;
import com.joe.doc.repository.FileInfoRepository;
import com.joe.doc.repository.GridFsRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;


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

    @Resource
    private GridFsRepository gridFsRepository;

    @Override
    public BaseRepository<FileInfo> getRepository() {
        return this.fileInfoRepository;
    }

    public ResponseResult upload(List<MultipartFile> multipartFiles) {
        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            log.info("Filename: {}", originalFilename);
            File file = this.writeFileToLocalFile(multipartFile);
            String objectId = this.gridFsRepository.store(file);
            TikaModel tikaModel = this.fileParserComponent.parse(file);
            FileInfo fileInfo = FileInfo.builder()
                    .filename(originalFilename)
                    .length(multipartFile.getSize())
                    .contentType(multipartFile.getContentType())
                    .metadata(tikaModel.getMetadataAsMap())
                    .build();
            fileInfo.setId(objectId);
            log.info("File Content: {}", tikaModel.getContent());
            this.fileInfoRepository.insert(fileInfo);
        }
        return ResponseResult.success();
    }

    private File writeFileToLocalFile(MultipartFile multipartFile) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            File localFile = new File("D:\\tmp\\" + originalFilename);
            multipartFile.transferTo(localFile);
            return localFile;
        } catch (IOException e) {
            throw new RuntimeException("文件写入本地异常.", e);
        }

    }
}
