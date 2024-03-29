package com.joe.doc.repository;

import cn.hutool.core.io.FileUtil;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Joe BlackZ
 * @description Mongodb GridFS
 * @date 2020/2/15 16:07
 */
@Slf4j
@Repository
public class GridFsRepository {

    @Resource
    private GridFsTemplate gridFsTemplate;

    /**
     * Store file into mongodb gridFs
     *
     * @param inputStream file stream
     * @param filename    filename
     * @return file objectId in gridFs
     */
    public String store(InputStream inputStream, String filename) {
        ObjectId store = this.gridFsTemplate.store(inputStream, filename);
        return store.toHexString();
    }

    /**
     * Store file info mongodb gridFs
     *
     * @param file file
     * @return file objectId in gridFs
     */
    public String store(File file) {
        try (BufferedInputStream bufferedInputStream = FileUtil.getInputStream(file)) {
            return this.store(bufferedInputStream, file.getName());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * remove file in gridFs
     *
     * @param objectId file objectId
     */
    public void remove(Object objectId) {
        Criteria id = Criteria.where("_id").is(objectId);
        this.gridFsTemplate.delete(new Query(id));
    }

    public void remove(Object[] objectId) {
        Criteria id = Criteria.where("_id").in(objectId);
        this.gridFsTemplate.delete(new Query(id));
    }

    public GridFSFile getGridFsFile(String objectId) {
        Criteria id = Criteria.where("_id").in(objectId);
        return this.gridFsTemplate.findOne(new Query(id));
    }

    public GridFsResource getGridFsFileResource(String objectId) {
        GridFSFile gridFsFile = this.getGridFsFile(objectId);
        return this.gridFsTemplate.getResource(gridFsFile);
    }
}
