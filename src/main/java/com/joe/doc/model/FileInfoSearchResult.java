package com.joe.doc.model;

import lombok.Builder;
import lombok.Data;

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
}
