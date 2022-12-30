package com.joe.doc.service;

import com.joe.doc.common.ResponseResult;
import com.joe.doc.model.BaseEntity;
import com.joe.doc.constant.ResponseMessage;
import com.joe.doc.repository.BaseRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @description base service
 * @author JoezBlackZ
 * @date 2020/1/3 23:36
 */
@Slf4j
public abstract class BaseService<T extends BaseEntity> {

    /**
     * repository
     *
     * @return runtime repository
     */
    public abstract BaseRepository<T> getRepository();

    /**
     *
     *
     * @param t 要保存的数据
     * @return 返回保存结果
     */
    public ResponseResult save(T t) {
        try {
            T insert = this.getRepository().insert(t);
            if (insert != null && insert.getId() != null) {
                return ResponseResult.success().msg(ResponseMessage.SAVE_SUCCESS).data(insert);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.SAVE_FAIL);
    }

    /**
     * 批量保存
     *
     * @param collection 要保存的数据
     * @return 返回保存结果
     */
    public ResponseResult saveAll(Collection<T> collection) {
        try {
            Collection<T> results = this.getRepository().insertAll(collection);
            if (!results.isEmpty()) {
                return ResponseResult.success().msg(ResponseMessage.SAVE_SUCCESS);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.SAVE_FAIL);
    }


    public ResponseResult modifyById(BaseEntity baseEntity) {
        try {
            long l = this.getRepository().updateById(baseEntity);
            if (l > 0) {
                return ResponseResult.success().msg(ResponseMessage.UPDATE_SUCCESS).data(l);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.UPDATE_FAIL);
    }

    public ResponseResult removeByIds(Collection<? extends Serializable> collections) {
        return getResponseResult(this.getRepository().deleteByIds(collections));
    }


    public ResponseResult removeByIds(Object[] ids) {
        return getResponseResult(this.getRepository().deleteByIds(ids));
    }

    private ResponseResult getResponseResult(long l) {
        try {
            if (l > 0) {
                return ResponseResult.success().msg(ResponseMessage.DELETE_SUCCESS).data(l);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.DELETE_FAIL);
    }

    public ResponseResult removeById(Object id) {
        try {
            long l = this.getRepository().deleteById(id);
            if (l > 0) {
                return ResponseResult.success().msg(ResponseMessage.DELETE_SUCCESS).data(l);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.DELETE_FAIL);
    }


    private ResponseResult findAll() {
        try {
            List<T> all = this.getRepository().selectAll();
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(all);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult findAllByPage(int pageNum, int pageSize) {
        try {
            List<T> allByPage = this.getRepository().selectAllByPage(pageNum, pageSize);
            long count = this.getRepository().count();
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(allByPage).count(count);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult find(BaseEntity baseEntity) {
        try {
            List<T> all = this.getRepository().select(baseEntity);
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(all);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult findByPage(BaseEntity baseEntity, int page, int limit) {
        try {
            if (baseEntity == null) {
                return this.findAllByPage(page, limit);
            }
            List<T> byPage = this.getRepository().selectByPage(baseEntity, page, limit);
            long count = this.getRepository().count(baseEntity);
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(byPage).count(count);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult findOne(BaseEntity baseEntity) {
        try {
            T one = this.getRepository().selectOne(baseEntity);
            if (one != null) {
                return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(one);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult findById(Object id) {
        try {
            T byId = this.getRepository().selectById(id);
            if (byId != null) {

                return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(byId);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult getCount() {
        try {
            long count = this.getRepository().count();
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(count);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult getCount(BaseEntity baseEntity) {
        try {
            long count = this.getRepository().count(baseEntity);
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(count);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }

}
