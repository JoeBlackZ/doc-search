package com.joe.doc.config;

import com.joe.doc.model.TikaModel;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author zhangqi
 */
@Component
public class FileParserComponent {

    @Resource
    private Tika tika;

    public TikaModel parse(MultipartFile multipartFile) {
        if (Objects.isNull(multipartFile)) {
            return null;
        }
        try (BufferedInputStream buffer = new BufferedInputStream(multipartFile.getInputStream())) {
            return this.parse(buffer);
        } catch (IOException e) {
            throw new RuntimeException("文件解析异常.", e);
        }
    }

    public TikaModel parse(InputStream inputStream) {
        if (Objects.isNull(inputStream)) {
            return null;
        }
        try (BufferedInputStream buffer = new BufferedInputStream(inputStream)) {
            Metadata metadata = new Metadata();
            String content = this.tika.parseToString(buffer, metadata);
            return TikaModel.builder().content(content).metadata(metadata).build();
        } catch (IOException | TikaException e) {
            throw new RuntimeException("文件解析异常.", e);
        }
    }

}
