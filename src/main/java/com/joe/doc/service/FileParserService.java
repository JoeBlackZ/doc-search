package com.joe.doc.service;

import com.joe.doc.model.TikaModel;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author zhangqi
 */
@Component
public class FileParserService {

    @Resource
    private Tika tika;

    private final static int CORE_POOL_SIZE = 2;
    private final static int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 1800,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public TikaModel parse(File file) {
        if (Objects.isNull(file)) {
            return null;
        }
        try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
            return this.parse(buffer);
        } catch (IOException e) {
            throw new RuntimeException("文件解析异常.", e);
        }
    }

    public TikaModel parse(InputStream inputStream) {
        if (Objects.isNull(inputStream)) {
            return null;
        }
        try (InputStream buffer = inputStream instanceof BufferedInputStream ? inputStream : new BufferedInputStream(inputStream)) {
            Metadata metadata = new Metadata();
            String content = this.tika.parseToString(buffer, metadata);
            return TikaModel.builder().content(content).metadata(metadata).build();
        } catch (IOException | TikaException e) {
            throw new RuntimeException("文件解析异常.", e);
        }
    }

    public void submitFileParseTask(Runnable runnable) {
        this.executor.submit(runnable);
    }

}
