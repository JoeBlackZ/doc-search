package com.joe.doc.model;

import lombok.*;

import java.util.List;

/**
 * @author zhangqi
 */
@Data
@Builder
public class FileInfoSearchResult {
    private Long took;
    private Long hitCount;
    private List<FileInfoIndexModel> fileInfos;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Item extends FileInfoIndexModel {
        private String id;
        private Double scope;
    }
}
