package com.joe.doc.service;

import com.joe.doc.model.FileInfo;
import com.joe.doc.repository.BaseRepository;
import com.joe.doc.repository.FileInfoRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description sys user service
 * @author JoezBlackZ
 * @date 2020/1/4 10:08
 */
@Service
public class FileInfoService extends BaseService<FileInfo> {

    @Resource
    private FileInfoRepository fileInfoRepository;

    @Override
    public BaseRepository<FileInfo> getRepository() {
        return this.fileInfoRepository;
    }

}
