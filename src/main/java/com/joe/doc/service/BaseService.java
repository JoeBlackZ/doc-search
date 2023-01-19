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
 * @author JoezBlackZ
 * @description base service
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
     * @param t 要保存的数据
     * @return 返回保存结果
     */
    public ResponseResult<T> save(T t) {
        try {
            T insert = this.getRepository().insert(t);
            if (insert != null && insert.getId() != null) {
                return ResponseResult.success(insert, ResponseMessage.SAVE_SUCCESS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.SAVE_FAIL);
    }

    /**
     * 批量保存
     *
     * @param collection 要保存的数据
     * @return 返回保存结果
     */
    public ResponseResult<Collection<T>> saveAll(Collection<T> collection) {
        try {
            Collection<T> results = this.getRepository().insertAll(collection);
            if (!results.isEmpty()) {
                return ResponseResult.success(results, ResponseMessage.SAVE_SUCCESS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.SAVE_FAIL);
    }


    public ResponseResult<Long> modifyById(BaseEntity baseEntity) {
        try {
            long l = this.getRepository().updateById(baseEntity);
            if (l > 0) {
                return ResponseResult.success(l, ResponseMessage.UPDATE_SUCCESS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.UPDATE_FAIL);
    }

    public ResponseResult<Long> removeByIds(Collection<? extends Serializable> collections) {
        return getResponseResult(this.getRepository().deleteByIds(collections));
    }


    public ResponseResult<Long> removeByIds(Object[] ids) {
        return getResponseResult(this.getRepository().deleteByIds(ids));
    }

    private ResponseResult<Long> getResponseResult(long l) {
        try {
            if (l > 0) {
                return ResponseResult.success(l, ResponseMessage.DELETE_SUCCESS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.DELETE_FAIL);
    }

    public ResponseResult<Long> removeById(Object id) {
        try {
            long l = this.getRepository().deleteById(id);
            if (l > 0) {
                return ResponseResult.success(l, ResponseMessage.DELETE_SUCCESS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.DELETE_FAIL);
    }


    private ResponseResult<List<T>> findAll() {
        try {
            List<T> all = this.getRepository().selectAll();
            return ResponseResult.success(all, ResponseMessage.QUERY_SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult<List<T>> findAllByPage(int pageNum, int pageSize) {
        try {
            List<T> allByPage = this.getRepository().selectAllByPage(pageNum, pageSize);
            return ResponseResult.success(allByPage, ResponseMessage.QUERY_SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult<List<T>> find(BaseEntity baseEntity) {
        try {
            List<T> all = this.getRepository().select(baseEntity);
            return ResponseResult.success(all, ResponseMessage.QUERY_SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult<List<T>> findByPage(BaseEntity baseEntity, int page, int limit) {
        try {
            if (baseEntity == null) {
                return this.findAllByPage(page, limit);
            }
            List<T> byPage = this.getRepository().selectByPage(baseEntity, page, limit);
            return ResponseResult.success(byPage, ResponseMessage.QUERY_SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult<T> findOne(BaseEntity baseEntity) {
        try {
            T one = this.getRepository().selectOne(baseEntity);
            if (one != null) {
                return ResponseResult.success(one, ResponseMessage.QUERY_SUCCESS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult<T> findById(Object id) {
        try {
            T byId = this.getRepository().selectById(id);
            if (byId != null) {
                return ResponseResult.success(byId, ResponseMessage.QUERY_SUCCESS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.QUERY_FAIL);
    }

    public ResponseResult<Long> getCount() {
        try {
            long count = this.getRepository().count();
            return ResponseResult.success(count, ResponseMessage.QUERY_SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.QUERY_FAIL);
    }

    public ResponseResult<Long> getCount(BaseEntity baseEntity) {
        try {
            long count = this.getRepository().count(baseEntity);
            return ResponseResult.success(count, ResponseMessage.QUERY_SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.fail(ResponseMessage.QUERY_FAIL);
    }

}
