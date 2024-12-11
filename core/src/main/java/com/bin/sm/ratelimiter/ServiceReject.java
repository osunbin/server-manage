package com.bin.sm.ratelimiter;

import com.bin.sm.internal.http.client.HttpClient;
import com.bin.sm.ratelimiter.ExecutableResult.RejectionReason;
import com.bin.sm.util.StringUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceReject {


    private ConcurrentHashMap<String, ServiceRejectMeta> callerRejectMeta = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Long> callerNameFunction = new ConcurrentHashMap<>();


    public static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newScheduledThreadPool(2);

    // TODO
    public void start() {
        SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> cleanTimeLessThanLastMin(this.callerNameFunction), 5, 5, TimeUnit.MINUTES);
        SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> this.callerRejectMeta.keySet().forEach(this::refreshCallerMethod), 0, 1, TimeUnit.HOURS);
    }


    public ExecutableResult executable(String callerName,String funcName) {

        ServiceRejectMeta serviceRejectMeta = callerRejectMeta.get(callerName);

        if (serviceRejectMeta != null) {
            return analyzeExecutable(callerName,funcName, serviceRejectMeta);
        } else {
            synchronized (this) {
                if (this.callerRejectMeta.get(callerName) == null) {
                    this.refreshCallerMethod(callerName, true);

                }
                serviceRejectMeta = this.callerRejectMeta.get(callerName);
                if (serviceRejectMeta != null) {
                    return this.analyzeExecutable(callerName,funcName, serviceRejectMeta);
                }
            }

        }
        return new ExecutableResult().setExecutable(false).setReason(RejectionReason.REJECT);
    }


    public ExecutableResult analyzeExecutable(String callerName,String funcName,ServiceRejectMeta serviceRejectMeta) {
        // 服务端设置拒绝该调用方
        if (serviceRejectMeta.isReject()) {
            return new ExecutableResult().setExecutable(false).setReason(RejectionReason.REJECT);
        }

        if (isLimit(callerName,funcName)) {
            return new ExecutableResult().setExecutable(false).setReason(RejectionReason.LIMIT);
        }
        return new ExecutableResult().setExecutable(true);
    }




    boolean isLimit(String callerName,String funcName) {

        String callerFunction = generateKey(callerName, funcName);
        // 限流时间
        Long limitTime = callerNameFunction.get(callerName);
        if (limitTime != null) {
            if (limitTime / (1000 * 60) == System.currentTimeMillis() / (1000 * 60)) {
                // 分钟数不相等
                return true;
            } else {
                callerNameFunction.remove(callerFunction);
            }
        }
        return false;
    }

    public void refreshCallerMethod(String callerName) {
        refreshCallerMethod(callerName, false);
    }

    private void refreshCallerMethod(String callerName, boolean exceptionAddDefault) {
        try {
            ServiceRejectMeta serviceRejectMeta = HttpClient.get("/sm/getServiceRejectMeta?callerKey=" + callerName + "&serviceName=" + "serviceName", ServiceRejectMeta.class);
            processServiceRejectMeta(callerName, exceptionAddDefault, serviceRejectMeta);
        } catch (IOException e) {
            e.printStackTrace();
            if (exceptionAddDefault) {
                callerRejectMeta.put(callerName, new ServiceRejectMeta().setCallerName(callerName).setReject(false).setNoCallerUsage(true));
            }
        }
    }

    public void processServiceRejectMeta(String callerName, boolean exceptionAddDefault, ServiceRejectMeta serviceRejectMeta) {
        if (serviceRejectMeta != null) {
            callerRejectMeta.put(callerName, serviceRejectMeta);
        } else if (exceptionAddDefault){
            callerRejectMeta.put(callerName, new ServiceRejectMeta().setCallerName(callerName).setReject(false).setNoCallerUsage(true));

        }
    }

    public void limit(String caller, String function, long time) {
        if (StringUtil.isBlank(caller)) {
            return;
        }
        if (StringUtil.isBlank(function)) {
            // 调用函数限流
            this.callerNameFunction.put(generateKey(caller, function), time);
        }
    }

    void cleanTimeLessThanLastMin(ConcurrentHashMap<String, Long> callerKeyFunction) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1);
        long time = calendar.getTimeInMillis();

        callerKeyFunction.forEach((key, value) -> {
            if (value < time) {
                callerKeyFunction.remove(key);
            }
        });
    }

    private String generateKey(String callerName, String function) {
        return callerName + "#" + function;
    }

}
