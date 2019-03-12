package com.dcits.comet.dao.param;


import com.dcits.comet.dao.ParamSupport;
import com.dcits.comet.dao.exception.ParamException;
import com.dcits.comet.dao.model.BasePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author chengliang
 * @Description 参数操作
 * @Date 2019-03-05 18:37
 * @Version 1.0
 **/
@Component
@Slf4j
public class ParamSupportImpl implements ParamSupport {

    @Autowired
    private ParamDaoSupport paramDaoSupport;

    @Override
    public <T extends BasePo> Integer count(T entity) {
        if (CacheUtil.isNotParamTable(entity)) {
            throw new ParamException(entity.getClass().getSimpleName() + "不允许缓存结果");
        }
        String cacheKey = CacheUtil.getCacheKey(entity);
        return paramDaoSupport.count(entity, cacheKey);
    }


    @Override
    public <T extends BasePo> List<T> selectList(T entity) {
        if (CacheUtil.isNotParamTable(entity)) {
            throw new ParamException(entity.getClass().getSimpleName() + "不允许缓存结果");
        }
        String cacheKey = CacheUtil.getCacheKeyAll(entity);
        return paramDaoSupport.selectList(entity, cacheKey);
    }

    /**
     * 主键通用查询
     *
     * @param entity
     * @param pkValue
     * @return
     */
    @Override
    public <T extends BasePo> T selectByPrimaryKey(T entity, Object... pkValue) {
        if (CacheUtil.isNotParamTable(entity)) {
            throw new ParamException(entity.getClass().getSimpleName() + "不允许缓存结果");
        }
        String cacheKey = CacheUtil.getCacheKey(entity);
        return paramDaoSupport.selectByPrimaryKey(entity, cacheKey, pkValue);
    }

    /**
     * 主键通用动态更新
     *
     * @param entity
     * @return
     */
    @Override
    public <T extends BasePo> int updateByPrimaryKey(T entity) {
        if (CacheUtil.isNotParamTable(entity)) {
            throw new ParamException(entity.getClass().getSimpleName() + "不允许缓存结果");
        }
        String cacheKey = CacheUtil.getCacheKey(entity);
        return paramDaoSupport.updateByPrimaryKey(entity, cacheKey);
    }

    /**
     * 根据主键删除记录
     *
     * @param entity
     * @return
     */
    @Override
    public <T extends BasePo> int deleteByPrimaryKey(T entity) {
        if (CacheUtil.isNotParamTable(entity)) {
            throw new ParamException(entity.getClass().getSimpleName() + "不允许缓存结果");
        }
        String cacheKey = CacheUtil.getCacheKey(entity);
        return paramDaoSupport.deleteByPrimaryKey(entity, cacheKey);
    }


    @Override
    public <T extends BasePo> List<T> selectAll(T entity) {
        if (CacheUtil.isNotParamTable(entity)) {
            throw new ParamException(entity.getClass().getSimpleName() + "不允许缓存结果");
        }
        String cacheKey = CacheUtil.getCacheKeyAll(entity);
        return paramDaoSupport.selectAll(entity, cacheKey);
    }


    @Override
    public <T extends BasePo> int insert(T entity) {
        if (CacheUtil.isNotParamTable(entity)) {
            throw new ParamException(entity.getClass().getSimpleName() + "不允许缓存结果");
        }
        String cacheKey = CacheUtil.getCacheKey(entity);
        return paramDaoSupport.insert(entity, cacheKey);
    }


    @Override
    public <T extends BasePo> int insert(List<T> list) {
        if (CacheUtil.isNotParamTable(list.get(0))) {
            throw new ParamException(list.get(0).getClass().getSimpleName() + "不允许缓存结果");
        }
        String cacheKey = CacheUtil.getCacheKeyAll(list.get(0));
        return paramDaoSupport.insert(list, cacheKey);
    }
}
