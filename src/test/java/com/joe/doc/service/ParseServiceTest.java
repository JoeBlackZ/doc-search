package com.joe.doc.service;

import com.joe.doc.entity.TikaModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ParseServiceTest {

    @Resource
    private ParseService parseService;

    @Test
    void parse() {

    }

    @Test
    void testParse() {
        String filename = "E:\\dev\\document\\SQL\\MySQL.sql";
        TikaModel parse = this.parseService.parse(new File(filename));
        log.info(parse.getMetadataAsMap().toString());
        log.info(parse.getContent());
    }
}