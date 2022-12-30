package com.joe.doc.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
public class SysRole extends BaseEntity{

    private String roleName;

    private String roleNameZh;

    private List<String> sysMenus;
}
