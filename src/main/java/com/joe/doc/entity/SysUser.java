package com.joe.doc.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * system user info
 *
 * @author JoezBlackZ
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class SysUser extends BaseEntity{

    private String username;

    private String nickname;

    private String password;

    private String phoneNumber;

    private String email;

    private String avatar;

    private String deptId;

    private List<String> roles;

    private Date lastPasswordResetDate;

}
