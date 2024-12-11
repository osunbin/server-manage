package com.bin.sm.aop;

import java.lang.instrument.Instrumentation;

public class MyPreMain {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("MyPreMain.premain agentArgs is: " + agentArgs);
        try {
            Class<?> aClass = Class.forName("com.bin.sm.Config");
            aClass.getDeclaredConstructor().newInstance();

            System.out.println("path-" + System.getProperty("configPath"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
