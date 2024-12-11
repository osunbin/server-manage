package com.bin.sm.extension.rocketmq.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import org.apache.rocketmq.common.protocol.header.SendMessageRequestHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RocketmqProducerSendInterceptor extends AbstractInterceptor {


    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        if (context.getArguments()[3] instanceof SendMessageRequestHeader header) {
            String oldProperties = header.getProperties();
            String newProperties = this.insertTags2Properties(oldProperties);
            header.setProperties(newProperties);
        }
        return context;
    }

    private String insertTags2Properties(String oldProperties) {
        StringBuilder newProperties = new StringBuilder();
        Map<String, List<String>> trafficTag = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : trafficTag.entrySet()) {
       //for (Map.Entry<String, List<String>> entry : TrafficUtils.getTrafficTag().getTag().entrySet()) {
            String key = entry.getKey();
//            if (!TagKeyMatcher.isMatch(key)) {
//                continue;
//            }
            List<String> values = entry.getValue();
            newProperties.append(key);
            newProperties.append(1);
            newProperties.append(values == null || values.isEmpty() ? null : values.getFirst());
            newProperties.append(2);
        }
        if (newProperties.length() == 0) {
            return oldProperties;
        }
        if (oldProperties == null || oldProperties.length() == 0) {
            // The header for rocketmq is empty, and the separator at the end of the new header needs to be removed
            newProperties.deleteCharAt(newProperties.length() - 1);
            return newProperties.toString();
        }
        return newProperties.append(oldProperties).toString();
    }

}
