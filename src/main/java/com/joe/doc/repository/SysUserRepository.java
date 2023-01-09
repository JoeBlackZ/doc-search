package com.joe.doc.repository;

import cn.hutool.core.util.StrUtil;
import com.joe.doc.model.SysUser;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author JoezBlackZ
 * @description SysUserRepository
 * @date 2020/1/3 22:22
 */
@Repository
public class SysUserRepository extends BaseRepository<SysUser> {

    public SysUser selectByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        List<SysUser> sysUsers = this.mongoTemplate.find(new Query(), SysUser.class);
        return sysUsers.isEmpty() ? null : sysUsers.get(0);
    }

}
