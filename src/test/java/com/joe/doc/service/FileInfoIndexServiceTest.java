package com.joe.doc.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class FileInfoIndexServiceTest {

    @Resource
    private ElasticsearchClient esClient;

    @Test
    public void testEsClusterInfo() throws IOException {
        InfoResponse infoResponse = this.esClient.info();
        String clusterName = infoResponse.clusterName();
        log.info("clusterName: {}", clusterName);
    }
}