package com.bin.sm.command;

import java.util.List;

public class RpcCommand extends BaseCommand {
    // 0-app 1-group 2-func
    protected String type;
    protected String service;
    protected List<String> resources;
}
