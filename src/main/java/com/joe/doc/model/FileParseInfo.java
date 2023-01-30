package com.joe.doc.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

/**
 * @author JoeBlackZ
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class FileParseInfo extends BaseEntity {

    private String md5;
    private Map<String, Object> metadata;
    private String fileCreator;
    private Date fileCreateDate;
    private String fileLastModified;
    private Date fileLastModifiedDate;
    @CreatedDate
    private Date fileParseDate;

}
