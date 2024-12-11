package com.bin.sm.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {

    private static final Logger logger = LoggerFactory.getLogger("Microservice Governance");


    public static void info(String msg) {
        logger.info(msg);
    }


    public static void info(String format, Object arg) {
        logger.info(format, arg);
    }


    public static void info(String format, Object arg1, Object arg2) {
        logger.info(format, arg1, arg2);
    }


    public static void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }


    public static void info(String msg, Throwable t) {
        logger.info(msg, t);
    }




    public static void warn(String msg) {
        logger.warn(msg);
    }


    public static void warn(String format, Object arg) {
        logger.warn(format, arg);
    }


    public static void warn(String format, Object... arguments) {
        logger.warn(format, arguments);
    }


    public static void warn(String format, Object arg1, Object arg2) {
        logger.warn(format, arg1, arg2);
    }


    public static void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }



    public static void error(String msg) {
        logger.error(msg);
    }


    public static void error(String format, Object arg) {
        logger.error(format, arg);
    }


    public static void error(String format, Object arg1, Object arg2) {
        logger.error(format, arg1, arg2);
    }


    public static void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }


    public static void error(String msg, Throwable t) {
        logger.error(msg, t);
    }



}
