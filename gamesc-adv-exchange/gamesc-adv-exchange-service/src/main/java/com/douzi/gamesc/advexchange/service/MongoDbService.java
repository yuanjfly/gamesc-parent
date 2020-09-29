package com.douzi.gamesc.advexchange.service;

import java.util.List;
import org.springframework.data.mongodb.core.query.Query;

public interface MongoDbService<T> {

    /**
     * 保存对象
     * @param t
     * @return
     */
    public void saveObj(T t);

    /**
     * 查询所有
     * @return
     */
    public List<T> findAll();

    /***
     * 根据id查询
     * @param id
     * @return
     */
    public T getObjectById(String id);


    /**
     * 更新对象
     *
     * @param t
     * @return
     */
    public void updateObject(T t) ;

    /***
     * 删除对象
     * @param t
     * @return
     */
    public void deleteObject(T t);

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    public void deleteObjectById(String id);

    /**
     * 模糊查询
     * @param search
     * @return
     */
    public List<T> findByLikes(String search);

    /**
     * 按条件、排序查询
     * @param query
     * @param orderFiled
     * @param sortTag
     * @return
     */
    public T  findLastOne(Query query, String orderFiled, String sortTag);
}

