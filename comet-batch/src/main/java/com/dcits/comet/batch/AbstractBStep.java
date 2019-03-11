package com.dcits.comet.batch;


import com.dcits.comet.batch.param.BatchContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBStep<T,O> implements IBStep<T,O> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractBStep.class);


    @Override
    public int getCountNum(BatchContext batchContext,String node) {
        return 0;
    }

    @Override
    public List<String> getNodeList(BatchContext batchContext) {
        List<String> list = new ArrayList();
        list.add((String)null);
        return list;
    }

    @Override
    public void preBatchStep(BatchContext batchContext) {
       // logger.debug("preBatchStep");
    }

    @Override
    public List<T> getPageList(BatchContext batchContext, int offset, int pageSize,String node) {
        List<T> list = new ArrayList();
        list.add((T)null);
        return list;
    }

//    @Override
//    public T read() {
//        return null;
//    }

    @Override
    public O process(BatchContext batchContext,T item) {
        return (O)item;
    }

    @Override
    public void writeChunk(BatchContext batchContext,List<O> items) {
        for(O item:items){
            writeOne(batchContext,item);
        }
    }

    public abstract void writeOne(BatchContext batchContext,O item);

    @Override
    public void afterBatchStep(BatchContext batchContext) {

    }


}
