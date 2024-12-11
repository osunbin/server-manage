package com.bin.sm.executor.support;

@FunctionalInterface
public interface TaskDecorator {
    Runnable decorate(Runnable runnable);
}