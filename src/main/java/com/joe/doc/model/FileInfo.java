package com.joe.doc.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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
public class FileInfo extends BaseEntity {

    private String filename;
    private String contentType;
    private Long length;
    private String fileExtName;
    @CreatedDate
    private Date fileUploadDate;
    private String fileUploadUserId;
}
