package com.joe.doc.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import com.joe.doc.model.FileInfoIndexModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


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

    @Test
    public void testIndexDocument() throws IOException {
        String id = "63b93aa1a895af2f23c6e421";
        FileInfoIndexModel infoIndexModel = FileInfoIndexModel.builder()
                .filename("新一代工业自动化平台_HiaPlant V7智慧管理系统用户需求说明书_D版.docm")
                .contentType("application/vnd.ms-word.document.macroEnabled.12")
                .fileLength(407564L)
                .fileExtName("docx")
                .content("""
                        新一代工业自动化平台_HiaPlant V7智慧管理系统用户需求说明书_D版
                        新一代工业自动化平台_HiaPlant V7智慧管理系统用户需求说明书_D版
                        新一代工业自动化平台_HiaPlant V7智慧管理系统用户需求说明书_D版
                        """)
                .build();
        IndexRequest<FileInfoIndexModel> request = IndexRequest.of(i -> i
                .index("file_info")
                .id(id)
                .document(infoIndexModel)
        );
        IndexResponse indexResponse = this.esClient.index(request);
        log.info("id: {}, version: {}", indexResponse.id(), indexResponse.version());
    }

    @Test
    public void testDeleteDocumentById() throws IOException {
        String id = "63b93aa1a895af2f23c6e421";
        DeleteRequest deleteRequest = DeleteRequest.of(i -> i.index("file_info").id(id));
        DeleteResponse deleteResponse = this.esClient.delete(deleteRequest);
        log.info("id: {}, version: {}", deleteResponse.id(), deleteResponse.version());
    }
}