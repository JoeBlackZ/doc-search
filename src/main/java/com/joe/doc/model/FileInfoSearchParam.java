package com.joe.doc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangqi
 */
@Data
@Schema
public class FileInfoSearchParam {

    @Schema(description = "搜索关键字", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keywords;
    @Schema(description = "搜索范围", defaultValue = "all", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String searchScope;
    @Schema(description = "搜索类型", defaultValue = "SimpleQueryString, MatchPhrase", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String searchType;
    @Schema(description = "文件后缀", defaultValue = "docx, xlsx", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fileExtName;
    @Schema(description = "文件上传时间", defaultValue = "2023/01/05 13:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date fileUploadDate;

}
