package com.bin.sm.config;

import java.io.File;

public class ConfigFactory {


    public static Configs loadConfigs() {
        String javaProtocol = "file";
        Configs configs = null;
        if ("file".equals(javaProtocol)) {
             configs = ConfigLoader.loadFromFile("D:\\code\\self\\sm-core\\core\\src\\main\\resources\\agent.yml");
        } else if ("jar".equals(javaProtocol)) {
            configs = JarFileConfigLoader.load("agent.yml");
        }
        return configs;
    }
}
