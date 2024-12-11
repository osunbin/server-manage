package com.bin.sm.executor.dynamic;

public class ThreadPoolParameter {

    private String tpId;

    private String threadPoolKey;

    /**
     * Core pool size
     */
    private Integer corePoolSize;

    /**
     * Maximum pool size
     */
    private Integer maximumPoolSize;

    /**
     * Queue name
     */
    private String queueName;

    /**
     * Capacity
     */
    private Integer capacity;

    /**
     * Keep alive time
     */
    private Integer keepAliveTime;


    /**
     * Rejected name
     */
    private String rejectedName;

    /**
     * Allow core thread timeout
     */
    private boolean allowCoreThreadTimeOut;



    public String getTpId() {
        return tpId;
    }

    public void setTpId(String tpId) {
        this.tpId = tpId;
    }


    public String getThreadPoolKey() {
        return threadPoolKey;
    }

    public void setThreadPoolKey(String threadPoolKey) {
        this.threadPoolKey = threadPoolKey;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }


    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Integer keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }


    public String getRejectedName() {
        return rejectedName;
    }

    public void setRejectedName(String rejectedName) {
        this.rejectedName = rejectedName;
    }

    public boolean isAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }
}
