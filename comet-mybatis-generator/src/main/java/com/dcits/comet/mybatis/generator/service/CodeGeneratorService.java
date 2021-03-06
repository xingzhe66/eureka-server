package com.dcits.comet.mybatis.generator.service;

import com.dcits.comet.mybatis.generator.entity.GeneratorProperties;
import com.dcits.comet.mybatis.generator.mapper.CodeGeneratorMapper;
import com.dcits.comet.mybatis.generator.util.PbUtils;
import com.dcits.comet.mybatis.generator.util.TemplateHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CodeGeneratorService {
    public static final String TRUE = "true";

    @Autowired
    private CodeGeneratorMapper codeDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGeneratorService.class);
    private static Map<String,Object> validationMap =new HashMap<>();

    /**
     * 生成代码入口
     *    需判断一次生成全部表还是指定一张表
     *
     * @return
     * @throws Exception
     */

    public void createCode(GeneratorProperties generatorProperties) throws Exception {
        String isCrateAllTable = generatorProperties.getIsCrateAllTable();
        //生成所有表
            if (!PbUtils.isEmpty(isCrateAllTable) && TRUE.equals(isCrateAllTable.toLowerCase())) {
                List<Map<String, Object>> listTables = getTablesList(generatorProperties);
                if(listTables.size()>0){
                    for (int i = 0; i < listTables.size(); i++) {
                        generatorProperties.setTableName((String) listTables.get(i).get("TABLE_NAME"));
                        String tableComment=(String) listTables.get(i).get("TABLE_COMMENT");
                        String tableType=null;
                        if(tableComment!=null && tableComment.toUpperCase().contains("PARAM")){
                            tableType="PARAM";
                        }
                        if(tableComment!=null && tableComment.toUpperCase().contains("LEVEL")){
                            tableType="LEVEL";
                        }
                        if(tableComment!=null && tableComment.toUpperCase().contains("UPRIGHT")){
                            tableType="UPRIGHT";
                        }
                        generatorProperties.setTableType(tableType);
                        generatorProperties.setEntityDescription(tableComment);
                        createCodeFolw(generatorProperties);
                    }
                }else{
                    LOGGER.info("数据库不存在,请确认generator.properties配置文件的generator.dbName配置是否正确");
                }
            } else {
                String tableComment=getTableComment(generatorProperties);
                if(tableComment!=null && tableComment.toUpperCase().contains("PARAM")){
                    generatorProperties.setTableType("PARAM");
                }
                if(tableComment!=null && tableComment.toUpperCase().contains("LEVEL")){
                    generatorProperties.setTableType("LEVEL");
                }
                if(tableComment!=null && tableComment.toUpperCase().contains("UPRIGHT")){
                    generatorProperties.setTableType("UPRIGHT");
                }
                generatorProperties.setEntityDescription(tableComment);
                createCodeFolw(generatorProperties);
            }
          //生成校验配置文件
           String isCreateValidation = generatorProperties.getIsCreateValidation();
          if (!PbUtils.isEmpty(isCreateValidation) && TRUE.equals(isCreateValidation.toLowerCase()) && !validationMap.isEmpty()) {
              Map<String,Object> datas=new HashMap<>();
              datas.put("validation",validationMap);
              createValidation(datas,generatorProperties);
            LOGGER.info(generatorProperties.getDbName() + "校验信息文件生成完成");
          }



    }
    /**
     *生成对应文件
     */
    public void createCodeFolw(GeneratorProperties generatorProperties)throws Exception{
        List<String> tableKeys=getTableKeys(generatorProperties);
        generatorProperties.setTablePkSize(tableKeys.size());
        Map<String, Object> modelDate= getTemplateData(getTableComumnModel(tableKeys,generatorProperties),generatorProperties);
        if(!modelDate.isEmpty()){
//            //生成entity
           createEntity(modelDate,generatorProperties);
//            //生成Mapper.Xml
            createMapperXml(modelDate,generatorProperties);
            //生成Mapper_ext.Xml
            String iscreateMapperExt = generatorProperties.getIsCreateMapperExt();
            if (!PbUtils.isEmpty(iscreateMapperExt) && TRUE.equals(iscreateMapperExt.toLowerCase())) {
                createMapperExt(modelDate,generatorProperties);
            }
            LOGGER.info(generatorProperties.getTableName() + "表对应的JavaBean和Mapper文件生成");

        }else{
            LOGGER.info("generator.properties配置文件的generator.dbName或者generator.tableName不存在");
        }
    }





    /**
     * 生成mybatis mapper文件
     * @throws Exception
     */
    public void createMapperXml(Map<String, Object> modelDate,GeneratorProperties generatorProperties) throws Exception {
        String ftlName = "/MybatisMapper.ftl";
        // 生成文件的路径和名称
        String fileName = generatorProperties.getBasedir() + "/src/main/resources/" + generatorProperties.getMapperPackage()+"/" + PbUtils.convertToCamelCase(generatorProperties.getTableName()) + "Mapper.xml";
        TemplateHelp.creatTemplate(modelDate, ftlName, fileName);
    }
    /**
     * 生成mybatis mapper_ext文件
     * @throws Exception
     */
    public void  createMapperExt(Map<String, Object> modelDate,GeneratorProperties generatorProperties)throws Exception {
        String ftlName = "/MybatisMapper_ext.ftl";
        // 生成文件的路径和名称
        String fileName = generatorProperties.getBasedir() + "/src/main/resources/" + generatorProperties.getMapperPackage()+"/" + PbUtils.convertToCamelCase(generatorProperties.getTableName()) + "Mapper_ext.xml";
        TemplateHelp.creatTemplate(modelDate, ftlName, fileName);
    }


    /**
     * 生成JavaBean 实体类
     * @throws Exception
     */
    public void createEntity(Map<String, Object> modelDate,GeneratorProperties generatorProperties) throws Exception {
        String ftlName = "/entity.ftl";
        // 生成文件的路径和名称
        String fileName = generatorProperties.getBasedir() + "/src/main/java/" + generatorProperties.getBasePackage().replace(".", "/") + "/"+ generatorProperties.getEntityPackage()+"/" + PbUtils.convertToCamelCase(generatorProperties.getTableName()) + "Po.java";
        TemplateHelp.creatTemplate(modelDate, ftlName, fileName);
    }

    /**
     * 生成校验配置信息
     * @throws Exception
     */
    public void createValidation(Map<String, Object> modelDate,GeneratorProperties generatorProperties) throws Exception {
        String ftlName =  "/validation.ftl";
        // 生成文件的路径和名称
        String fileName = generatorProperties.getBasedir() + "/src/main/resources/ValidationMessages.properties";
        TemplateHelp.creatTemplate(modelDate, ftlName, fileName);
    }



    /**
     * 获取所有的表名称及描述
     * @return
     */
    public List<Map<String, Object>> getTablesList(GeneratorProperties generatorProperties) throws Exception {
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("dbName", generatorProperties.getDbName());
            params.put("dbType", generatorProperties.getDbType());//数据库类型
            maps = codeDao.getTablesList(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }
    /**
     * 获取表的描述信息
     */
    public String getTableComment(GeneratorProperties generatorProperties) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", generatorProperties.getTableName());// 表名称
        params.put("dbName", generatorProperties.getDbName());// 数据库名称
        params.put("dbType", generatorProperties.getDbType());//数据库类型
        String tableComment = codeDao.getTableComment(params);
        return tableComment;
    }

    /**
     * 获取表得主键
     */
    public List<String> getTableKeys(GeneratorProperties generatorProperties) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", generatorProperties.getTableName());// 表名称
        params.put("dbName", generatorProperties.getDbName());// 数据库名称
        params.put("dbType", generatorProperties.getDbType());//数据库类型
        List<String> tableKeys = codeDao.getTableKeys(params);
        return tableKeys;

    }

    /**
     * 获取数据表的列信息
     *
     * @return
     */
    private List<Map> getListMap(GeneratorProperties generatorProperties) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", generatorProperties.getTableName());// 表名称
        params.put("dbName", generatorProperties.getDbName());// 数据库名称
        params.put("dbType", generatorProperties.getDbType());//数据库类型
        List<Map> maps = codeDao.getListMap(params);
        return maps;
    }

    /**
     * 处理数据表列信息
     * @return
     */
    public List<Map<String, Object>> getTableComumnModel(List<String> tableKeys,GeneratorProperties generatorProperties) {
        List<Map> list = getListMap(generatorProperties);
        List<Map<String, Object>> clList = new ArrayList<Map<String, Object>>();
        if (list != null && list.size() > 0) {
            int pkIndex=1;
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> oMap = new HashMap<String, Object>();
                // 遍历list
                Map<String, Object> map = list.get(i);
                // 遍历map
                for (String key : map.keySet()) {
                    // 列名称
                    if ("COLUMNNAME".equals(key)) {
                        String columnName=map.get(key).toString();//列名称
                        String reStr = PbUtils.strRelplacetoLowerCase(columnName);// 列名称，首字母小写，去下划线
                        oMap.put("columnNameL", columnName);// 列名称
                        oMap.put("columnName", reStr);// 列名称，首字母小写，去下划线
                        // 自动判断大小写
                        if ("_".equals(map.get(key).toString().substring(1, 2))) {
                            oMap.put("UpUmnName", reStr);// 列名称，首字母小写，去下划线
                        } else {
                            oMap.put("UpUmnName", PbUtils.fristStrToUpperCase(reStr));// 列名称，首字母大写，去下划线
                        }
                        //主键标识  用于model
                        String cloumsTop = null;
                        //主键标识  用于xml
                        String pkFlag="N";
                        //判断是否为主键
                        if(tableKeys.size()>0 && tableKeys.contains(columnName)){
                            cloumsTop ="@TablePk(index="+pkIndex+")";
                            pkIndex++;
                            pkFlag ="Y";
                        }
                        oMap.put("cloumsTop", cloumsTop);
                        oMap.put("pkFlag", pkFlag);

                        //标识分库键
                        String partitonKey=null;
                        String shardFlag="N";
                        if("LEVEL".equals(generatorProperties.getTableType()) && columnName.toUpperCase().equals(generatorProperties.getShardColumn().toUpperCase())){
                            shardFlag="Y";
                            partitonKey="@PartitionKey";
                        }
                        oMap.put("partitonKey", partitonKey);
                        oMap.put("shardFlag",shardFlag);
                    }
                    // 注释
                    if(map.get("COLUMNCOMMENT")==null){
                        oMap.put("columnComment", " " );
                    }
                    if ("COLUMNCOMMENT".equals(key)) {
                        oMap.put("columnComment", map.get(key) == null ? " " : map.get(key));// 注释
                    }
                    // 列类型
                    if ("COLUMNTYPE".equals(key)) {
                        String columnType = map.get("COLUMNTYPE").toString();// 列类型
                        oMap.put("dbType", columnType);
                        oMap.put("javaType", PbUtils.convertJavaType(columnType));
                        oMap.put("jdbcType", PbUtils.convertJdbcType(columnType));
                    }
                    if(oMap.get("columnComment")!=null && ""!=oMap.get("columnComment")){
                        validationMap.put((String) oMap.get("columnName"),oMap.get("columnComment"));
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
    public Map<String, Object> getTemplateData(List<Map<String, Object>> clList,GeneratorProperties generatorProperties) {
        Map<String, Object> data = new HashMap<String, Object>();
        if (clList != null && clList.size() > 0) {
            String className=PbUtils.convertToCamelCase(generatorProperties.getTableName())+"Po";
            data.put("className", className);// 类名称
            data.put("objectName", PbUtils.fristStrToLowerCase(className));// 类名首字母小写
            data.put("mouldName", generatorProperties.getBasePackage());// 基本包名称
            data.put("entityPackage", generatorProperties.getEntityPackage());//entity包名称
            data.put("functionComment", generatorProperties.getEntityDescription()==null?" ":generatorProperties.getEntityDescription());// 功能说明
            data.put("tableName", generatorProperties.getTableName().toUpperCase());// 表名称
            data.put("cloums", clList);// 属性
            data.put("author", generatorProperties.getAuthor());// 作者
            data.put("date", PbUtils.getCurrentDateTime());// 日期
            data.put("entityParentClass", generatorProperties.getEntityParentClass());// entity父类
            if(generatorProperties.getTablePkSize()>0){
                data.put("tablePkSize","Y");//存在主键
            }else{
                data.put("tablePkSize","N");//不存在主键
            }
            data.put("tableType", generatorProperties.getTableType());//表类型 PARAM(参数表)  LEVEL（水平分库）  UPRIGHT（垂直分库）

        }
        return data;
    }
}


