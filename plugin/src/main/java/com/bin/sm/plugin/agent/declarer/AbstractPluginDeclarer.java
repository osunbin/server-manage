package com.bin.sm.plugin.agent.declarer;

public abstract class AbstractPluginDeclarer implements PluginDeclarer {

    @Override
    public SuperTypeDeclarer[] getSuperTypeDeclarers() {
        return new SuperTypeDeclarer[0];
    }
}
