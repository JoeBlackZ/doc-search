package com.joe.doc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhangqi
 */
@Data
@Schema
public class FileInfoSearchParam {

    @Schema(description = "搜索关键字", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keywords;
    @Schema(description = "搜索范围", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String searchScope;
    @Schema(description = "搜索类型", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String searchType;

}
