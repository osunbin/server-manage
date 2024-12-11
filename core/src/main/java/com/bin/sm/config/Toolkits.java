package com.bin.sm.config;

public class Toolkits {


    public void init() {
        try {
            Class<?> aClass = Class.forName("com.bin.sm.toolkit.config.ConfigLoader");
            aClass.getDeclaredConstructor().newInstance();

            System.out.println("path-" + System.getProperty("configPath"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
