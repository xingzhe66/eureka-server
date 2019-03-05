package com.dcits.comet.mybatis.generator.entity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "generator")
@PropertySource("classpath:/config/generator.properties")
public class GeneratorEntity {
    private String basedir;
    private String basePackage;
    private String entityPackage;
    private String mapperPackage;
    private String isDeleteTablePrefix;
    private String tableName;
    private String tablePrefix;
    private String author;
    private String entityDescription;
    private String dbName;
    private String dbType;
    private String isCrateAllTable;
    private String iscreateMapperExt;



    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public String getIsDeleteTablePrefix() {
        return isDeleteTablePrefix;
    }

    public void setIsDeleteTablePrefix(String isDeleteTablePrefix) {
        this.isDeleteTablePrefix = isDeleteTablePrefix;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEntityDescription() {
        return entityDescription;
    }

    public void setEntityDescription(String entityDescription) {
        this.entityDescription = entityDescription;
    }

    public String getIsCrateAllTable() {
        return isCrateAllTable;
    }

    public void setIsCrateAllTable(String isCrateAllTable) {
        this.isCrateAllTable = isCrateAllTable;
    }

    public String getIsCreateMapperExt() {
        return iscreateMapperExt;
    }

    public void setIsCreateMapperExt(String iscreateMapperExt) {
        this.iscreateMapperExt = iscreateMapperExt;
    }

    @Override
    public String toString() {
        return "DefineEntity{" +
                "basedir='" + basedir + '\'' +
                ", basePackage='" + basePackage + '\'' +
                ", entityPackage='" + entityPackage + '\'' +
                ", mapperPackage='" + mapperPackage + '\'' +
                ", isDeleteTablePrefix='" + isDeleteTablePrefix + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tablePrefix='" + tablePrefix + '\'' +
                ", author='" + author + '\'' +
                ", shuoming='" + entityDescription + '\'' +
                ", dbName='" + dbName + '\'' +
                ", dbType='" + dbType + '\'' +
                '}';
    }

}
