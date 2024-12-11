package com.bin.sm;

import com.bin.sm.plugin.agent.declarer.PluginDeclarer;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        scanDeclarers();
        System.out.println("Hello world!");
    }

    private static List<? extends PluginDeclarer> scanDeclarers() {
        final List<PluginDeclarer> declares = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .ignoreClassVisibility()
                .acceptPackages("com.bin.sm.extension.*")
                .scan()) {
            ClassInfoList allClasses = scanResult.getAllClasses();
            for (ClassInfo classInfo : allClasses) {
                boolean impl = classInfo.implementsInterface("com.bin.sm.plugin.agent.declarer.PluginDeclarer");

                if (impl) {
                    System.out.println(classInfo.getName());
                }
            }

        }

        return declares;
    }
}