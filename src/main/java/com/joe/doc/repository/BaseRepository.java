package com.joe.doc.repository;

import com.joe.doc.entity.BaseEntity;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @description  base repository
 * @author  JoezBlackZ
 * @date  2020/1/3 22:03
 */
@Slf4j
@SuppressWarnings("unchecked")
public abstract class BaseRepository<T> {

    /**
     * spring data mongodb template
     */
    @Resource
    protected MongoTemplate mongoTemplate;

    /**
     * subclass
     */
    protected Class<T> entityClass;

    /**
     * BaseRepository constructor
     * get subclass entity class in constructor
     */
    BaseRepository() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = (genericSuperclass).getActualTypeArguments();
        entityClass = (Class<T>) actualTypeArguments[0];
    }

    /**
     * insert one document
     *
     * @param entity the data
     * @return entity with id
     */
    public T insert(T entity) {
        return this.mongoTemplate.insert(entity);
    }

    /**
     * batch insert
     *
     * @param list tha data
     * @return data list with id
     */
    public List<T> insertAll(Collection<T> list) {
        return (List<T>) this.mongoTemplate.insertAll(list);
    }

    /**
     * 根据id更新数据
     * 只要实体参数中的属性不为null就更新该属性
     *
     * @param baseEntity 更新参数
     * @return 返回更新条数
     */
    public long updateById(final BaseEntity baseEntity) {
        try {
            String id = baseEntity.getId();
            Query query = new Query(Criteria.where("_id").is(id));
            Update update = new Update();
            Field[] fields = baseEntity.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object o = field.get(baseEntity);
                if (o != null) {
                    update.set(field.getName(), o);
                }
            }
            UpdateResult updateResult = this.mongoTemplate.updateFirst(query, update, entityClass);
            return updateResult.getModifiedCount();
        } catch (IllegalAccessException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return 0;
    }

    /**
     * delete multiple document
     *
     * @param ids one or multiple id
     * @return count of delete
     */
    public final long deleteByIds(final Object... ids) {
        final Query query = new Query(Criteria.where("_id").in(ids));
        final DeleteResult deleteResult = this.mongoTemplate.remove(query, entityClass);
        return deleteResult.getDeletedCount();
    }

    /**
     * 根据多个id删除数据
     *
     * @param ids 集合参数id
     * @return 返回删除的数量
     */
    public long deleteByIds(final Collection<? extends Serializable> ids) {
        final Query query = new Query(Criteria.where("_id").in(ids));
        DeleteResult deleteResult = this.mongoTemplate.remove(query, entityClass);
        return deleteResult.getDeletedCount();
    }

    /**
     * 根据id删除单条数据
     *
     * @param id 主键id
     * @return 返回删除数量
     */
    public long deleteById(final Object id) {
        DeleteResult deleteResult = this.mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), entityClass);
        return deleteResult.getDeletedCount();
    }

    /**
     * 查询所有数据
     *
     * @return 返回所有的数据
     */
    public List<T> selectAll() {
        return this.mongoTemplate.findAll(entityClass);
    }

    /**
     * 分页查询数据
     *
     * @param page  页码
     * @param limit 每页条数
     * @return 返回该页数据
     */
    public List<T> selectAllByPage(int page, int limit) {
        Query query = new Query().skip((long)(page - 1) * limit).limit(limit);
        return this.mongoTemplate.find(query, entityClass);
    }

    /**
     * 根据实体对象的参数查询结果集
     * 只要实体对象中的属性部位null即为查询条件
     *
     * @param baseEntity 查询条件
     * @return 返回匹配的结果
     */
    public List<T> select(BaseEntity baseEntity) {
        try {
            Criteria criteria = this.getCriteria(baseEntity);
            return this.mongoTemplate.find(new Query(criteria), entityClass);
        } catch (IllegalAccessException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    /**
     * 根据实体对象中的属性以及其中的分页信息查询数据
     *
     * @param baseEntity query parameters
     * @param page current page
     * @param limit size of each page
     * @return match result
     */
    public List<T> selectByPage(BaseEntity baseEntity, int page, int limit) {
        try {
            Criteria criteria = this.getCriteria(baseEntity);
            Query query = new Query(criteria).skip((page - 1) * (long)limit).limit(limit);
            return this.mongoTemplate.find(query, entityClass);
        } catch (IllegalAccessException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    /**
     * 查询一条数据，既是匹配到多个结果也只返回第一条
     *
     * @param baseEntity 查询参数
     * @return 返回匹配结果
     */
    public T selectOne(BaseEntity baseEntity) {
        try {
            return this.mongoTemplate.findOne(new Query(this.getCriteria(baseEntity)), entityClass);
        } catch (IllegalAccessException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 根据id查询一条结果
     *
     * @param id 主键id
     * @return 返回一条数据
     */
    public T selectById(Object id) {
        return this.mongoTemplate.findById(id, entityClass);
    }

    /**
     * 统计该集合中的数据量
     *
     * @return 数据量
     */
    public long count() {
        return this.mongoTemplate.count(new Query(), entityClass);
    }

    /**
     * 根据条件查询数据量
     *
     * @param baseEntity 查询参数
     * @return 数据量
     */
    public long count(BaseEntity baseEntity) {
        try {
            return this.mongoTemplate.count(new Query(this.getCriteria(baseEntity)), entityClass);
        } catch (IllegalAccessException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return 0;
    }

    /**
     * 根据实体参数利用反射获取其中的值组成查询条件
     *
     * @param baseEntity 查询参数
     * @return 返回查询条件
     * @throws IllegalAccessException 非法访问异常
     */
    private Criteria getCriteria(BaseEntity baseEntity) throws IllegalAccessException {
        Criteria criteria = new Criteria();
        if (baseEntity.getId() != null) {
            criteria.and("_id").is(baseEntity.getId());
        }

        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object o = field.get(baseEntity);
            if (o != null) {
                criteria.and(field.getName()).is(o);
            }
        }
        return criteria;
    }


}
