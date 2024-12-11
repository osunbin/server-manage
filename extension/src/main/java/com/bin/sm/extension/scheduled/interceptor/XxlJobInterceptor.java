package com.bin.sm.extension.scheduled.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import com.xxl.job.core.biz.model.ReturnT;

public class XxlJobInterceptor extends AbstractInterceptor {


    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        // 服务关闭
        context.skip(new ReturnT<String>(ReturnT.FAIL_CODE, "server is shutdown."));


        return context;
    }

}
