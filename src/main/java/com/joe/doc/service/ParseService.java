package com.joe.doc.service;

import cn.hutool.core.io.FileUtil;
import com.joe.doc.entity.TikaModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;

/**
 * @Description parse service
 * @Author JoeBlackZ
 * @Date 2020/1/5 11:57
 */
@Slf4j
@Service
public class ParseService {

    @Resource
    private Tika tika;

    /**
     * parse inputStream and build parse result
     * The inputStream will be close inputStream when after parse
     *
     * @param inputStream file inputStream data
     * @return parse result
     */
    public TikaModel parse(final InputStream inputStream) {
        try {
            if (inputStream == null || inputStream.available() == 0) {
                return null;
            }
            Metadata metadata = new Metadata();
            String content = this.tika.parseToString(inputStream, metadata);
            if (log.isDebugEnabled()) {
                log.info("parse file finished.");
            }
            return TikaModel.builder()
                    .content(content)
                    .metadata(metadata)
                    .build();
        } catch (IOException | TikaException e) {
            if (log.isErrorEnabled()) {
                log.error("parse file error", e);
            }
        }
        return null;
    }

    /**
     * Parse file
     *
     * @param file file need to be parsed
     * @return parse result
     */
    public TikaModel parse(File file) {
        try {
            if (FileUtil.exist(file)) {
                return this.parse(new FileInputStream(file));
            }
        } catch (FileNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("could not find the file when parsing.", e);
            }
        }
        return null;
    }

}
