package com.joe.doc.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author zhangqi
 */
@Slf4j
@Service
public class FileInfoIndexService {

    @Resource
    private ElasticsearchClient esClient;

    public void indexes() {

    }

}
