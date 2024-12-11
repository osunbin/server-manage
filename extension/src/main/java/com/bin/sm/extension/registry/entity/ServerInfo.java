package com.bin.sm.extension.registry.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ServerInfo extends BaseInfo {
    /**
     * Region
     */
    private String zone;

    /**
     * Namespace
     */
    private String project;

    /**
     * Environment
     */
    private String environment;

    /**
     * The name of the service
     */
    private String serviceName;

    /**
     * The type of operation
     */
    private String operateType;

    /**
     * The name of the app
     */
    private String applicationName;

    /**
     * Group name
     */
    private String groupName;

    /**
     * Version number
     */
    private String version;

    /**
     * Consanguinity information
     */
    private List<Consanguinity> consanguinityList;

    /**
     * Contract Information
     */
    private List<Contract> contractList;

    /**
     * Registration Information
     */
    private Map<String, BaseInfo> registryInfo;

    /**
     * Service ID
     */
    private String instanceId;

    /**
     * expiration date
     */
    private Date validateDate;

    /**
     * A collection of instance IDs
     */
    private List<String> instanceIds;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Consanguinity> getConsanguinityList() {
        return consanguinityList;
    }

    public void setConsanguinityList(List<Consanguinity> consanguinityList) {
        this.consanguinityList = consanguinityList;
    }

    public List<Contract> getContractList() {
        return contractList;
    }

    public void setContractList(List<Contract> contractList) {
        this.contractList = contractList;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Map<String, BaseInfo> getRegistryInfo() {
        return registryInfo;
    }

    public void setRegistryInfo(Map<String, BaseInfo> registryInfo) {
        this.registryInfo = registryInfo;
    }

    public Date getValidateDate() {
        return validateDate;
    }

    public void setValidateDate(Date validateDate) {
        this.validateDate = validateDate;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }
}
