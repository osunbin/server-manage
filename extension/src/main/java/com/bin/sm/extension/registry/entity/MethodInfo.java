package com.bin.sm.extension.registry.entity;

import java.util.List;

public class MethodInfo {
    /**
     * The name of the method
     */
    private String name;

    /**
     * Participation set
     */
    private List<ParamInfo> paramInfoList;

    /**
     * Return value information
     */
    private ParamInfo returnInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParamInfo> getParamInfoList() {
        return paramInfoList;
    }

    public void setParamInfoList(List<ParamInfo> paramInfoList) {
        this.paramInfoList = paramInfoList;
    }

    public ParamInfo getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(ParamInfo returnInfo) {
        this.returnInfo = returnInfo;
    }
}
