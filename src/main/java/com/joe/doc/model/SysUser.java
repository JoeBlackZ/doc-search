package com.joe.doc.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
public class SysUser extends BaseEntity {

    private String username;
    private String nickname;
    private String password;
    private String phoneNumber;
    private String email;
    private String avatar;
    private String deptId;
    @CreatedDate
    private Date createDate;
    private String createUserId;
    @LastModifiedDate
    private Date updateDate;
    private String updateUserId;
}
