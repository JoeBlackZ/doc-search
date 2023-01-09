package com.joe.doc.model;

import lombok.*;
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
public class FileInfo extends BaseEntity{

    private String filename;
    private String contentType;
    private Long length;

}
