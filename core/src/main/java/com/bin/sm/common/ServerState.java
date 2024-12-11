package com.bin.sm.common;

public enum ServerState {

    Normal(1,"normal"), // 正常
    Reboot(2,"reboot"), // 重启
    Shutdown(3,"shutdown"); // 关机


    private int state;
    private String name;
    ServerState(int state, String name) {
        this.state = state;
        this.name = name;
    }




    public int state() {
        return state;
    }

    public String stateName(){
        return name;
    }

}