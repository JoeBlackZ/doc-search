package com.joe.doc.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

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
public class SysMenu extends BaseEntity{

    private String name;

    private String url;

    private String path;

    private String component;

    private String iconClass;

    private String keepAlive;

    private String requireAuth;

    private String parentId;

}
