package com.bin.sm.context;

public interface NodeAdapter {

    String getEnv();

    String getZone();

    String getAddressStr();

    String getIp();

    void setWeight(int weight);

    int getWeight();

    boolean existTag(String tag);

    boolean notTag();


    long getStartTime();

}
