package com.bin.sm.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class LogManager {

    private static void run() {
        System.setProperty("logging.root.level","INFO");
        loadLog();
    }


    public static void loadLog() {
        ConfigurationSource source;
        try {
            InputStream resourceAsStream = LogManager.class.getResourceAsStream("/sm/log4j2.xml");
            source = new ConfigurationSource(resourceAsStream);
            Configurator.initialize(null, source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void logUseed() {
        Logger logger = LoggerFactory.getLogger(LogManager.class);

        logger.info("thread-{}",Thread.currentThread().getName());

        // appName、文件目录、日志级别、启用console,debug那个级别配置
        LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        Level level = ctx.getLogger("com.bin.sm").getLevel();
        logger.info("日志级别:{}",level);

        // Configurator.setLevel("com.bin.sm", Level.DEBUG);

        // Configurator.setRootLevel(Level.DEBUG);

    }
}
