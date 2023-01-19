package com.joe.doc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangqi
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileInfoIndexModel {

    private String filename;
    private Long fileLength;
    private String contentType;
    private String fileExtName;
    private String content;

}
