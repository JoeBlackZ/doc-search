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
public class SysMenu extends BaseEntity {

    private String name;
    private String url;
    private String path;
    private String component;
    private String iconClass;
    private String keepAlive;
    private String requireAuth;
    private String parentId;
    @CreatedDate
    private Date createDate;
    private String createUserId;
    @LastModifiedDate
    private Date updateDate;
    private String updateUserId;
}
