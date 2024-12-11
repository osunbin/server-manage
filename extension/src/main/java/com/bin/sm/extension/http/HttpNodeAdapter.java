package com.bin.sm.extension.http;

import com.bin.sm.context.NodeAdapter;
import com.bin.sm.util.StringUtil;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class HttpNodeAdapter implements NodeAdapter {

    private ServiceInstance serviceInstance;



    public HttpNodeAdapter(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public Map<String, String> getMetadata() {
       return serviceInstance.getMetadata();
    }

    public String getEnv() {
        return getMetadata().getOrDefault("env","local");
    }

    public String getZone() {
        return getMetadata().getOrDefault("zone","defaultZone");
    }

    public boolean existTag(String tag) {
        String tags = getMetadata().get("tag");
        if (StringUtil.isEmpty(tags)) {
            return false;
        }
        List<String> list = Arrays.asList(tags.split(","));
        return list.contains(tag);
    }

    public boolean notTag() {
        String tags = getMetadata().get("tag");
        if (StringUtil.isEmpty(tags)) {
            return true;
        }
        return false;
    }


    public String getIp() {
        return serviceInstance.getHost();
    }

    public String getAddressStr() {
        return serviceInstance.getHost() + ":" + serviceInstance.getPort();
    }


    public long getStartTime(){
       return Integer.parseInt(getMetadata().getOrDefault("startTime","0"));
    }

    public void setWeight(int weight) {
        getMetadata().put("weight", String.valueOf(weight));
    }

    public int getWeight() {
       return Integer.parseInt(getMetadata().getOrDefault("weight","100"));
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }


}
