package com.bin.sm.metric.jvm;

class NativeImageChecker {
    static final boolean isGraalVmNativeImage = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    private NativeImageChecker() {
    }
}