package com.dcits.comet.dao;

import com.dcits.comet.dao.model.BasePo;

import java.util.List;

/**
 * @Author chengliang
 * @Description //TODO
 * @Date 2019-03-05 18:28
 * @Version 1.0
 **/
public interface BaseDaoSupport {

    String POSTFIX_COUNT = ".count";
    String POSTFIX_SELECTONE = ".selectOne";
    String POSTFIX_INSERT = ".insert";
    String POSTFIX_UPDATE = ".update";
    String POSTFIX_UPDATE_BY_ENTITY = ".updateByEntity";
    String POSTFIX_DELETE = ".delete";
    String POSTFIX_SELECTLIST = ".selectList";

    String UPDATE_BY_PRIMARYKEY = ".updateByPrimaryKey";

    String DELETE_BY_PRIMARYKEY = ".deleteByPrimaryKey";

    String SELECT_BY_PRIMARYKEY = ".selectByPrimaryKey";


    <T extends BasePo> Integer count(T entity);

    <T extends BasePo> int insert(T entity);

    <T extends BasePo> int insert(List<T> list);

    //<T extends BasePo> int update(T setParameter, T whereParameter);

    //<T extends BasePo> int delete(T entity);

    <T extends BasePo> List<T> selectList(T entity);

    /**
     * 主键通用查询
     *
     * @param entity
     * @param pkValue
     * @param <T>
     * @return
     */
    <T extends BasePo> T selectByPrimaryKey(T entity, Object... pkValue);

    /**
     * 主键通用动态更新
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T extends BasePo> int updateByPrimaryKey(T entity);

    /**
     * 根据主键删除记录
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T extends BasePo> int deleteByPrimaryKey(T entity);

}
