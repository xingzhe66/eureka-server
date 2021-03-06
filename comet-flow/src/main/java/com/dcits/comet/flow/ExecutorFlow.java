package com.dcits.comet.flow;

import com.dcits.comet.commons.utils.SpringContextUtil;
import org.springframework.stereotype.Component;

/**
 * @Author chengliang
 * @Description //TODO
 * @Date 2019-02-26 18:18
 * @Version 1.0
 **/
@Component
public class ExecutorFlow {
    public <IN, OUT> OUT start(String beanName, IN input) {
        IFlow flow = (IFlow) SpringContextUtil.getBean(beanName);
        return (OUT) flow.handle(beanName, input);
    }
}
