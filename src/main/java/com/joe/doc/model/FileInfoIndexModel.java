package com.joe.doc.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileInfoIndexModel {

    private String filename;
    private Long fileLength;
    private String contentType;
    private String fileExtName;
    private String content;

}
