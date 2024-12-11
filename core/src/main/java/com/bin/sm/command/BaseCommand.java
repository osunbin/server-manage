package com.bin.sm.command;

import com.bin.sm.util.JsonUtil;

import java.util.List;

public abstract class BaseCommand {

    protected int code;

    public int getCode() {
        return this.code;
    }

    public String toContent() {
        return this.code + JsonUtil.toJson(this);
    }


    public BaseCommand setCode(int code) {
        this.code = code;
        return this;
    }

}
