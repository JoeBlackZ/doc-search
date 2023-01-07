package com.joe.doc.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
//@SpringBootTest
class ParseServiceTest {

    @Test
    void dateFormatTest() {
        DateTime dateTime = DateUtil.parseUTC("2021-04-08T02:19:00Z");
        log.info(dateTime.toString());
    }
}