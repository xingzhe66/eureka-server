package com.dcits.comet.mybatis.generator.util;

import freemarker.template.*;

import java.io.*;
import java.util.Locale;
import java.util.Map;

/**
 * @功能说明：模板引擎
 * @作者： herun
 * @创建日期：2014-2-24
 * @版本号：V1.0
 */
public class TemplateHelp {


    /**
     * 模板路径
     *
     * @param data     数据模型
     * @param ftlName  ftl模板文件名称
     * @param fileName 生成文件的文件名称 例如：d:\TAction.java
     * @throws IOException
     * @throws TemplateException
     */
    public static void creatTemplate(Map<String, Object> data, String ftlName, String fileName) throws IOException, TemplateException {
        Configuration cfg = new Configuration();
        // 设置字符集编码
        cfg.setEncoding(Locale.CHINA, "UTF-8");
        // 加载模板文件
        cfg.setClassForTemplateLoading(new TemplateHelp().getClass(), "/ftl");
        // 设置对象包装器
        cfg.setObjectWrapper(new DefaultObjectWrapper());

        // 设计异常处理器
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);

        // 获取指定模板文件
        Template template = cfg.getTemplate(ftlName);

        // 检查路径是否存在，不存在则创建
        String path = fileName.substring(0, fileName.lastIndexOf("."));
        // 目标文件
        File saveFile = new File(path);
        // 判断存放路径是否存在，存在就删除，不存在就创建
        if (saveFile.exists()) {
            saveFile.delete();
        } else {
            saveFile.getParentFile().mkdirs();
        }

        // 定义输入文件，默认生成在工程根目录，设置文件的编码格式
        Writer out = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");

        // 最后开始生成
        template.process(data, out);

        // 关闭资源，否则删除不掉
        out.close();
    }
}
