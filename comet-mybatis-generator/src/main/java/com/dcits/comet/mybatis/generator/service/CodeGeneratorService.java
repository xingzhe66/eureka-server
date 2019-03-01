package com.dcits.comet.mybatis.generator.service;

import com.dcits.comet.mybatis.generator.entity.GeneratorEntity;
import com.dcits.comet.mybatis.generator.mapper.CodeGeneratorMapper;
import com.dcits.comet.mybatis.generator.util.PbUtils;
import com.dcits.comet.mybatis.generator.util.TemplateHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@EnableConfigurationProperties(GeneratorEntity.class)
public class CodeGeneratorService {
    @Autowired
    GeneratorEntity generatorEntity;
    @Autowired
    private CodeGeneratorMapper codeDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGeneratorService.class);

    /**
     * 生成代码入口
     *    需判断一次生成全部表还是指定一张表
     *
     * @return
     * @throws Exception
     */
    public void createCode() throws Exception {
        String isCrateAllTable = generatorEntity.getIsCrateAllTable();
        //生成所有表
            if (!PbUtils.isEmpty(isCrateAllTable) && "true".equals(isCrateAllTable.toLowerCase())) {
                List<Map<String, Object>> listTables = getTablesList();
                if(listTables.size()>0){
                    for (int i = 0; i < listTables.size(); i++) {
                        generatorEntity.setTableName((String) listTables.get(i).get("TABLE_NAME"));
                        generatorEntity.setEntityDescription((String) listTables.get(i).get("TABLE_COMMENT"));
                        createCodeFolw();
                    }
                }else{
                    LOGGER.info("数据库不存在,请确认generator.properties配置文件的generator.dbName配置是否正确");
                }
            } else {
                generatorEntity.setEntityDescription(getTableComment());
                createCodeFolw();
            }
    }
    /**
     *生成对应文件
     */
    public void createCodeFolw()throws Exception{
        Map<String, Object> modelDate= getTemplateData(getTableComumnModel());
        if(!modelDate.isEmpty()){
            //生成entity
            createEntity(modelDate);
            //生成Mapper.Xml
            createMapperXml(modelDate);
            //生成Mapper_ext.Xml
            createMapperExt(modelDate);
            LOGGER.info(generatorEntity.getTableName() + "表对应的JavaBean和Mapper文件生成");
        }else{
            LOGGER.info("generator.properties配置文件的generator.dbName或者generator.tableName不存在");
        }
    }

    /**
     * 生成mybatis mapper文件
     * @throws Exception
     */
    public void createMapperXml(Map<String, Object> modelDate) throws Exception {
        String ftlName = "/MybatisMapper.ftl";
        // 生成文件的路径和名称
        String fileName = generatorEntity.getBasedir() + "/" + generatorEntity.getBasePackage().replace(".", "/") + "/"+generatorEntity.getMapperPackage()+"/" + PbUtils.convertToCamelCase(generatorEntity.getTableName()) + "Mapper.xml";
        TemplateHelp.creatTemplate(modelDate, ftlName, fileName);
    }
    /**
     * 生成mybatis mapper_ext文件
     * @throws Exception
     */
    public void  createMapperExt(Map<String, Object> modelDate)throws Exception {
        String ftlName = "/MybatisMapper_ext.ftl";
        // 生成文件的路径和名称
        String fileName = generatorEntity.getBasedir() + "/" + generatorEntity.getBasePackage().replace(".", "/") + "/"+generatorEntity.getMapperPackage()+"/" + PbUtils.convertToCamelCase(generatorEntity.getTableName()) + "Mapper_ext.xml";
        TemplateHelp.creatTemplate(modelDate, ftlName, fileName);
    }

    /**
     * 生成JavaBean 实体类
     * @throws Exception
     */
    public void createEntity(Map<String, Object> modelDate) throws Exception {
        String ftlName = "/entity.ftl";
        // 生成文件的路径和名称
        String fileName = generatorEntity.getBasedir() + "/" + generatorEntity.getBasePackage().replace(".", "/") + "/"+generatorEntity.getEntityPackage()+"/" + PbUtils.convertToCamelCase(generatorEntity.getTableName()) + "Po.java";
        TemplateHelp.creatTemplate(modelDate, ftlName, fileName);
    }

    /**
     * 获取所有的表名称及描述
     * @return
     */
    public List<Map<String, Object>> getTablesList() throws Exception {
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("dbName", generatorEntity.getDbName());
            params.put("dbType", generatorEntity.getDbType());//数据库类型
            maps = codeDao.getTablesList(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }
    /**
     * 获取表的描述信息
     */
    public String getTableComment() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", generatorEntity.getTableName());// 表名称
        params.put("dbName", generatorEntity.getDbName());// 数据库名称
        params.put("dbType", generatorEntity.getDbType());//数据库类型
        String tableComment = codeDao.getTableComment(params);
        return tableComment;
    }

    /**
     * 获取数据表的列信息
     *
     * @return
     */
    private List<Map> getListMap() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", generatorEntity.getTableName());// 表名称
        params.put("dbName", generatorEntity.getDbName());// 数据库名称
        params.put("dbType", generatorEntity.getDbType());//数据库类型
        List<Map> maps = codeDao.getListMap(params);
        return maps;
    }

    /**
     * 处理数据表列信息
     * @return
     */
    public List<Map<String, Object>> getTableComumnModel() {
        List<Map> list = getListMap();
        List<Map<String, Object>> clList = new ArrayList<Map<String, Object>>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> oMap = new HashMap<String, Object>();
                // 遍历list
                Map<String, Object> map = list.get(i);
                // 遍历map
                for (String key : map.keySet()) {
                    // 列名称
                    if (key.equals("COLUMNNAME")) {
                        String reStr = PbUtils.strRelplacetoLowerCase(map.get(key).toString());// 列名称，首字母小写，去下划线
                        oMap.put("columnNameL", map.get(key).toString());// 列名称，首字母小写，去下划线
                        oMap.put("columnName", reStr);// 列名称，首字母小写，去下划线

                        // 自动判断大小写
                        if (map.get(key).toString().substring(1, 2).equals("_")) {
                            oMap.put("UpUmnName", reStr);// 列名称，首字母小写，去下划线
                        } else {
                            oMap.put("UpUmnName", PbUtils.fristStrToUpperCase(reStr));// 列名称，首字母大写，去下划线
                        }
                    }
                    // 注释
                    if (key.equals("COLUMNCOMMENT")) {
                        String reStr = PbUtils.strRelplacetoLowerCase(map.get("COLUMNNAME").toString());// 列名称，首字母小写，去下划线
                        oMap.put("columnComment", map.get(key) == null ? reStr : map.get(key));// 注释
                    }
                    // 列类型
                    if (key.equals("COLUMNTYPE")) {
                        String columnType = map.get("COLUMNTYPE").toString();// 列类型
                        oMap.put("javaType", PbUtils.convertJavaType(columnType));
                        oMap.put("dbType", columnType);
                        oMap.put("javaType", PbUtils.convertJavaType(columnType));
                        oMap.put("jdbcType", PbUtils.convertJdbcType(columnType));
                    }
                }
                clList.add(oMap);// 添加到集合
            }
        }
        return clList;
    }

    /**
     * 组装模板文件中所需的变量信息
     * @param clList
     * @return
     */
    public Map<String, Object> getTemplateData(List<Map<String, Object>> clList) {
        Map<String, Object> data = new HashMap<String, Object>();
        if (clList != null && clList.size() > 0) {
            String className=PbUtils.convertToCamelCase(generatorEntity.getTableName())+"Po";
            data.put("className", className);// 类名称
            data.put("objectName", PbUtils.fristStrToLowerCase(className));// 类名首字母小写
            data.put("mouldName", generatorEntity.getBasePackage());// 基本包名称
            data.put("entityPackage", generatorEntity.getEntityPackage());//entity包名称
            data.put("functionComment", generatorEntity.getEntityDescription());// 功能说明
            data.put("tableName", generatorEntity.getTableName().toUpperCase());// 表名称
            data.put("cloums", clList);// 属性
            data.put("author", generatorEntity.getAuthor());// 作者
            data.put("date", PbUtils.getCurrentDateTime());// 日期
        }
        return data;
    }
}

