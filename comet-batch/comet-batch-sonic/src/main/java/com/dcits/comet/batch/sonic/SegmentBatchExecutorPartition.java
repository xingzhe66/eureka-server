package com.dcits.comet.batch.sonic;

import com.alibaba.fastjson.JSON;
import com.dcits.comet.batch.AbstractSegmentStep;
import com.dcits.comet.batch.Segment;
import com.dcits.comet.batch.holder.SpringContextHolder;
import com.dcits.comet.batch.launcher.JobParam;
import com.dcits.comet.batch.param.BatchContext;
import com.dcits.sonic.executor.api.model.Attributes;
import com.dcits.sonic.executor.step.segment.AbstractStepSegmenter;
import com.dcits.sonic.executor.step.segment.SegmentRunningStep;
import com.dcits.sonic.executor.step.segment.SegmentedStepSender;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 分段
 *
 * @author leijian
 * @version 1.0
 * @date 2019/5/7 14:13
 **/
@Slf4j
public class SegmentBatchExecutorPartition extends AbstractStepSegmenter {

    @Override
    public void doSegment(SegmentRunningStep segmentRunningStep, SegmentedStepSender segmentedStepSender) {
        // 用户自定义拓展参数
        Attributes attributes = segmentRunningStep.getAttributes();
        Map<String, String> parameters = attributes.getAttributeMap();
        JobParam jobParam = JSON.parseObject(JSON.toJSONString(parameters), JobParam.class);
        // 执行分段逻辑，生成子分段扩展参数，生成的每个Attributes则为一个分段
        List<Attributes> attributesList = bulidAttributesList(jobParam, parameters);
        // 添加子分段扩展信息，
        segmentedStepSender.addBatch(attributesList);
    }

    //生成分段执行用到的拓展参数，如偏移量等
    private List<Attributes> bulidAttributesList(JobParam jobParam, Map<String, String> parameters) {
        //根据入参生成分段
        BatchContext batchContext = new BatchContext();
        batchContext.getParams().putAll(parameters);
        // 生成子分段扩展信息 ...
        List<Attributes> attributesList = new ArrayList<>();
        AbstractSegmentStep segmentStep = SpringContextHolder.getBean(jobParam.getStepName());
        List<String> nodes = segmentStep.getNodeList(batchContext);
        if (null == nodes || nodes.size() == 0) {
            return attributesList;
        }
        for (String node : nodes) {
            Map<String, String> attrMap = new HashMap<>();
            List<Segment> segments = segmentStep.getSegmentList(batchContext, node);
            if (null != segments && segments.size() != 0) {
                for (Segment segment : segments) {
                    attrMap.put("beginIndex", segment.getStartKey());
                    attrMap.put("endIndex", segment.getEndKey());
                    attrMap.put("pageSize", String.valueOf(segment.getRowCount()));
                    attrMap.put("node", node);
                    Attributes segAttributes = new Attributes(attrMap);
                    attributesList.add(segAttributes);
                }
            }
        }
        return attributesList;
    }
}
