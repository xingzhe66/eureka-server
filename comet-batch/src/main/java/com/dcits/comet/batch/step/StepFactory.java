package com.dcits.comet.batch.step;

import com.dcits.comet.batch.BatchBeanFactory;
import com.dcits.comet.batch.IBatchStep;
import com.dcits.comet.batch.IStep;
import com.dcits.comet.batch.ITaskletStep;
import com.dcits.comet.batch.constant.BatchConstant;
import com.dcits.comet.batch.exception.BatchException;
import com.dcits.comet.batch.holder.SpringContextHolder;
import com.dcits.comet.batch.launcher.CommonJobLauncher;
import com.dcits.comet.batch.listener.StepExeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class StepFactory {
    protected static final Log LOGGER = LogFactory.getLog(CommonJobLauncher.class);


    public static Step build(StepParam stepParam) {

        int pageSize = stepParam.getPageSize();
        int chunkSize = stepParam.getChunkSize();
        int beginIndex = stepParam.getBeginIndex();
        int endIndex = stepParam.getEndIndex();
        int threadNum = stepParam.getThreadNum();
        String stepName = stepParam.getStepName();

        ApplicationContext context = SpringContextHolder.getApplicationContext();

        StepBuilderFactory stepBuilders = context.getBean(StepBuilderFactory.class);

        IStep stepObj = (IStep) context.getBean(stepName);

        DataSourceTransactionManager dataSourceTransactionManager = (DataSourceTransactionManager) context.getBean("batchTransactionManager");
        Step step = null;
        if (stepObj instanceof IBatchStep) {

            ItemReader reader = BatchBeanFactory.getNewReader(stepName, pageSize, beginIndex, endIndex);
            ItemWriter writer = BatchBeanFactory.getNewWriter(stepName);
            ItemProcessor processor = BatchBeanFactory.getNewProcessor(stepName);

            StepExeListener stepListener = new StepExeListener();
            stepListener.setBatchStep((IBatchStep) stepObj);

            if (null == dataSourceTransactionManager) {
                LOGGER.warn("请配置数据库事务管理器！");
            }
            if (BatchConstant.RUN_TYPE_MULTI_THREAD.equals(stepParam.getRunType())) {

                SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
                step = stepBuilders.get("step_" + stepName)
                        .listener(stepListener)
                        .transactionManager(dataSourceTransactionManager)
                        .chunk(chunkSize)
                        //
                        .reader(reader)//.faultTolerant().skip(JsonParseException.class).skipLimit(1)
                        //.listener(new MessageItemReadListener(errorWriter))
                        .writer(writer)//.faultTolerant().skip(Exception.class).skipLimit(1)
                        //    .listener(new MessageWriteListener())
                        .processor(processor)
                        .taskExecutor(simpleAsyncTaskExecutor)
                        .throttleLimit(threadNum)
                        .build();

            } else {
                step = stepBuilders.get("step_" + stepName)
                        //.tasklet(tasklet)
                        .listener(stepListener)
                        .transactionManager(dataSourceTransactionManager)

                        .chunk(chunkSize)
                        .reader(reader)
                        // .reader(reader).faultTolerant().skip(JsonParseException.class).skipLimit(1)
                        // .listener(new MessageItemReadListener(errorWriter))
                        .writer(writer)//.faultTolerant().skip(Exception.class).skipLimit(1)
                        //.writer(writer).faultTolerant().skip(Exception.class).skipLimit(1)
                        //    .listener(new MessageWriteListener())
                        .processor(processor)
                        .build();
            }

        } else if (stepObj instanceof ITaskletStep) {
            ITaskletStep taskletStep = (ITaskletStep) stepObj;
            step = stepBuilders.get("step_" + stepName)
                    .transactionManager(dataSourceTransactionManager)
                    .tasklet(taskletStep)
                    .build();
        } else {
            throw new BatchException("不支持的Step类型");
        }
        return step;
    }

}