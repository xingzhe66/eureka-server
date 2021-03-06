package com.dcits.comet.dao.mybatis;

import com.dcits.comet.commons.Context;
import com.dcits.comet.commons.data.RowArgs;
import com.dcits.comet.commons.utils.BeanUtil;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.commons.utils.PageUtil;
import com.dcits.comet.dao.DaoSupport;
import com.dcits.comet.dao.annotation.TablePkScanner;
import com.dcits.comet.dao.interceptor.SelectForUpdateHelper;
import com.dcits.comet.dao.interceptor.SelectSegmentHelper;
import com.dcits.comet.dao.interceptor.TotalrecordHelper;
import com.dcits.comet.dao.model.BasePo;
import com.dcits.comet.dao.model.QueryResult;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author wangyun
 * @date 2019/3/21
 * @description DaoSupport实现
 */
public class DaoSupportImpl extends SqlSessionDaoSupport implements DaoSupport {
    static final Logger LOGGER = LoggerFactory.getLogger(DaoSupportImpl.class);
    private static Map<String, Map<String, String>> propertyColumnMapper = new HashMap();

    public DaoSupportImpl() {
    }

    public void initPropertyColumnMapper() {
        Collection<ResultMap> rms = this.getSqlSession().getConfiguration().getResultMaps();
        Iterator iter = rms.iterator();

        while (true) {
            Object object;
            do {
                if (!iter.hasNext()) {
                    return;
                }

                object = iter.next();
            } while (!(object instanceof ResultMap));

            ResultMap rm = (ResultMap) object;
            List<ResultMapping> list = rm.getResultMappings();
            Map<String, String> map = new HashMap(10);
            Iterator it = list.iterator();

            while (it.hasNext()) {
                ResultMapping r = (ResultMapping) it.next();
                map.put(r.getProperty(), r.getColumn());
            }

            propertyColumnMapper.put(rm.getId(), map);
        }
    }

    public static Map<String, Map<String, String>> getPropertyColumnMapper() {
        return propertyColumnMapper;
    }


    @Override
    public <T extends BasePo> Integer count(T entity) {
        String className = entity.getClass().getName();
        return (Integer) this.getSqlSession().selectOne(className + POSTFIX_COUNT, entity);
    }

    @Override
    public <T extends BasePo> Integer count(String statementPostfix, T object) {
        return (Integer) this.getSqlSession().selectOne(statementPostfix, object);
    }

    @Override
    public Integer count(String statementPostfix, Map<String, Object> parameter) {
        return (Integer) this.getSqlSession().selectOne(statementPostfix, parameter);
    }

    @Override
    public <T extends BasePo> T selectOne(T entity) {
        String className = entity.getClass().getName();
        return (T) this.getSqlSession().selectOne(className + POSTFIX_SELECTONE, entity);
    }

    @Override
    public <T extends BasePo> T selectOne(String statementPostfix, T object) {
        return (T) this.getSqlSession().selectOne(statementPostfix, object);
    }

    @Override
    public <T extends BasePo> T selectOne(String statementPostfix, Map<String, Object> parameter) {
        return (T) this.getSqlSession().selectOne(statementPostfix, parameter);
    }

    @Override
    public <T extends BasePo> T selectForUpdate(T parameter) {
        BasePo result;
        try {
            SelectForUpdateHelper.setSelectForUpdate();
            result = this.selectOne(parameter);
        } finally {
            SelectForUpdateHelper.cancelSelectForUpdate();
        }

        return (T) result;
    }


    @Override
    public <T extends BasePo> T selectForUpdate(String statementPostfix, T parameter) {
        BasePo result;
        try {
            SelectForUpdateHelper.setSelectForUpdate();
            result = this.selectOne(statementPostfix, parameter);
        } finally {
            SelectForUpdateHelper.cancelSelectForUpdate();
        }

        return (T) result;
    }

    @Override
    public <T extends BasePo> int insert(T entity) {
        String className = entity.getClass().getName();
        return this.getSqlSession().insert(className + POSTFIX_INSERT, entity);

    }

    @Override
    public <T extends BasePo> int insert(List<T> list) {
        int i = 0;

        BasePo bp;
        for (Iterator it = list.iterator(); it.hasNext(); i += this.insert(bp)) {
            bp = (BasePo) it.next();
        }

        return i;
    }

    @Override
    public <T extends BasePo> int insertBatch(List<T> list) {
        Assert.notEmpty(list,"insertBatch list is null");
        Assert.state(list.size() > 0, "insertBatch list size greater than 1");
        String className = list.get(0).getClass().getName();
        return this.getSqlSession().insert(className + POSTFIX_BATCH_INSERT, list);
    }

    @Override
    public <T extends BasePo> int update(T entity) {
        String className = entity.getClass().getName();
        return this.getSqlSession().update(className + POSTFIX_UPDATE, entity);
    }

    @Override
    public <T extends BasePo> int update(T setParameter, T whereParameter) {
        String className = setParameter.getClass().getName();
        return this.update(className + POSTFIX_UPDATE_BY_ENTITY, setParameter, whereParameter);
    }

    @Override
    public <T extends BasePo> int update(String statementPostfix, T setParameter, T whereParameter) {
        Map<String, Object> parameter = new HashMap(2);
        parameter.put("s", setParameter);
        parameter.put("w", whereParameter);
        return this.update(statementPostfix, parameter);
    }

    @Override
    public <T extends BasePo> int update(String statementPostfix, T entity) {
        return this.getSqlSession().update(statementPostfix, entity);
    }

    @Override
    public <T extends BasePo> int update(String statementPostfix, List<T> list) {
        int i = 0;
        BasePo bp;
        for (Iterator it = list.iterator(); it.hasNext(); i += this.update(statementPostfix, bp)) {
            bp = (BasePo) it.next();
        }

        return i;
    }

    @Override
    public int update(String statementPostfix, Map<String, Object> parameter) {
        return this.getSqlSession().update(statementPostfix, parameter);
    }

    @Override
    public <T extends BasePo> int delete(T entity) {
        String className = entity.getClass().getName();
        return this.getSqlSession().delete(className + POSTFIX_DELETE, entity);
    }

    @Override
    public <T extends BasePo> int delete(String statementPostfix, T entity) {
        return this.getSqlSession().delete(statementPostfix, entity);
    }

    @Override
    public int delete(String statementPostfix, Map<String, Object> parameter) {
        return this.getSqlSession().update(statementPostfix, parameter);
    }

    @Override
    public <T extends BasePo> List<T> selectList(T entity) {
        String statementPostfix = entity.getClass().getName() + POSTFIX_SELECTLIST;
        return this.selectList(statementPostfix, entity);
    }


    @Override
    public <T extends BasePo> List<T> selectAll(T entity) {
        throw new UnsupportedOperationException("The requested operation is not supported for BusinessTable !");
    }

    @Override
    public <T extends BasePo> List<T> selectList(String statementPostfix, T entity) {
        return this.getSqlSession().selectList(statementPostfix, entity);
    }

    @Override
    public <T extends BasePo> List<T> selectList(String statementPostfix, Map<String, Object> parameter, T entity) {
        return this.getSqlSession().selectList(statementPostfix, parameter);
    }

    @Override
    public List selectList(String statementPostfix, Map<String, Object> parameter) {
        return this.getSqlSession().selectList(statementPostfix, parameter);
    }

    @Override
    public <T extends BasePo> List<T> selectByPage(T entity) {
        RowArgs rowArgs = PageUtil.convertAppHead(Context.getInstance().getAppHead());
        if (BusiUtil.isNotNull(rowArgs)) {
            String statementPostfix = entity.getClass().getName() + ".selectList";
            return this.selectList(statementPostfix, entity, rowArgs.getPageIndex(), rowArgs.getLimit());
        } else {
            return this.selectList(entity);
        }
    }

    @Override
    public <T extends BasePo> List<T> selectList(T entity, int pageIndex, int pageSize) {
        String statementPostfix = entity.getClass().getName() + ".selectList";
        return this.selectList(statementPostfix, entity, pageIndex, pageSize);
    }

    @Override
    public <T extends BasePo> List<T> selectList(String statementPostfix, T entity, int pageIndex, int pageSize) {
        Boolean needTotalFlag = TotalrecordHelper.isNeadTotalRowCount();

        List result;
        try {
            TotalrecordHelper.setNeedTotalRowCount(false);
            result = this.getSqlSession().selectList(statementPostfix, entity, new RowBounds(pageIndex, pageSize));
        } finally {
            TotalrecordHelper.setNeedTotalRowCount(needTotalFlag);
        }

        return result;
    }

    @Override
    public <T extends BasePo> List<T> selectList(String statementPostfix, Map<String, Object> parameter, int pageIndex, int pageSize) {
        Boolean needTotalFlag = TotalrecordHelper.isNeadTotalRowCount();

        List result;
        try {
            TotalrecordHelper.setNeedTotalRowCount(false);
            result = this.getSqlSession().selectList(statementPostfix, parameter, new RowBounds(pageIndex, pageSize));
        } finally {
            TotalrecordHelper.setNeedTotalRowCount(needTotalFlag);
        }

        return result;
    }

    @Override
    public <T extends BasePo> QueryResult<T> selectQueryResult(T entity, int pageIndex, int pageSize) {
        String statementPostfix = entity.getClass().getName() + ".selectList";
        return this.selectQueryResult(statementPostfix, entity, pageIndex, pageSize);
    }

    @Override
    public <T extends BasePo> QueryResult<T> selectQueryResult(String statementPostfix, T entity, int pageIndex, int pageSize) {
        Boolean needTotalFlag = TotalrecordHelper.isNeadTotalRowCount();

        QueryResult result;
        try {
            TotalrecordHelper.setNeedTotalRowCount(true);
            List<T> resultList = this.getSqlSession().selectList(statementPostfix, entity, new RowBounds(pageIndex, pageSize));
            Long totalrecord = TotalrecordHelper.getTotalrecord();
            result = new QueryResult(resultList, totalrecord.longValue());
        } finally {
            TotalrecordHelper.setNeedTotalRowCount(needTotalFlag);
        }

        return result;
    }

    @Override
    public <T extends BasePo> QueryResult<T> selectQueryResult(String statementPostfix, Map<String, Object> parameter, int pageIndex, int pageSize) {
        Boolean needTotalFlag = TotalrecordHelper.isNeadTotalRowCount();

        QueryResult result;
        try {
            TotalrecordHelper.setNeedTotalRowCount(true);
            List<T> resultList = this.getSqlSession().selectList(statementPostfix, parameter, new RowBounds(pageIndex, pageSize));
            Long totalrecord = TotalrecordHelper.getTotalrecord();
            result = new QueryResult(resultList, totalrecord.longValue());
        } finally {
            TotalrecordHelper.setNeedTotalRowCount(needTotalFlag);
        }

        return result;
    }

    @Override
    public <T extends BasePo> List<T> selectListForUpdate(T entity) {
        List<BasePo> result;
        try {
            SelectForUpdateHelper.setSelectForUpdate();
            result = this.selectList(entity);
        } finally {
            SelectForUpdateHelper.cancelSelectForUpdate();
        }

        return (List<T>) result;
    }


    @Override
    public List selectSegmentList(String statementPostfix, Map<String, Object> parameter, int pageSize) {
        List result;
        try {
            SelectSegmentHelper.setSelectSegment();
            parameter.put("PAGE_SIZE", pageSize);
            result = this.selectList(statementPostfix, parameter);
        } finally {
            SelectSegmentHelper.cancelSelectSegment();
        }
        return result;
    }


    @Override
    public <T extends BasePo> List<T> selectListForUpdate(String statementPostfix, T entity) {
        List<BasePo> result;
        try {
            SelectForUpdateHelper.setSelectForUpdate();
            result = this.selectList(statementPostfix, entity);
        } finally {
            SelectForUpdateHelper.cancelSelectForUpdate();
        }

        return (List<T>) result;
    }

    final private <T extends BasePo> T getPkObject(T param, boolean ignoreNullValue, Object... pkValue) {
        String[] pks = TablePkScanner.pkColsScanner(param);
        // 表无主键
        if (pks.length == 0) {
            return null;
        }
        // 主键值为Null
        if (null == pkValue || pkValue.length == 0) {
            throw BusiUtil.createBusinessException("100502");
        }
        // 有效value
        boolean effectiveValue = false;
        for (Object v : pkValue) {
            if (null != v) {
                effectiveValue = true;
                break;
            }
        }
        // 无有效value
        if (!effectiveValue) {
            throw BusiUtil.createBusinessException("100503");
        }
        int i = 0;
        for (String pk : pks) {
            if (null != pkValue[i]) {
                try {
                    BeanUtil.setValue(param, pk, pkValue[i]);
                } catch (Exception e) {
                    throw BusiUtil.createBusinessException("100504", new String[]{pk});
                }
            } else if (!ignoreNullValue) {
                throw BusiUtil.createBusinessException("100501", new String[]{pk});
            }
            i++;
        }
        return param;
    }
}
