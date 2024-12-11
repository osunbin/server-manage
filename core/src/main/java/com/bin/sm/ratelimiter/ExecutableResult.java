package com.bin.sm.ratelimiter;

public class ExecutableResult {
    /**
     *  是否执行
     */
    private boolean executable;

    /**
     * 拒绝原因
     */
    private RejectionReason reason;

    public boolean isExecutable() {
        return executable;
    }

    public ExecutableResult setExecutable(boolean executable) {
        this.executable = executable;
        return this;
    }

    public RejectionReason getReason() {
        return reason;
    }

    public ExecutableResult setReason(RejectionReason reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public String toString() {
        return "SrvmgrExecutableResult{" + "executable=" + executable + ", reason=" + reason + '}';
    }

    public enum RejectionReason {
        /**
         * 抛弃
         */
        REJECT,
        /**
         * 限流
         */
        LIMIT;

    }
}
