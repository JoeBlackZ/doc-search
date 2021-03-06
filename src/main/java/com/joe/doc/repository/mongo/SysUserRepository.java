package com.joe.doc.repository.mongo;

import cn.hutool.core.util.StrUtil;
import com.joe.doc.entity.SysUser;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description  SysUserReponsitory
 * @author  JoezBlackZ
 * @date  2020/1/3 22:22
 */
@Repository
public class SysUserRepository extends BaseRepository<SysUser> {

    public SysUser selectByUsername(String username) {
        if (StrUtil.isNotBlank(username)) {
            List<SysUser> sysUsers = this.mongoTemplate.find(new Query(), SysUser.class);
            if (!sysUsers.isEmpty()) {
                return sysUsers.get(0);
            }
        }
        return null;
    }

}
