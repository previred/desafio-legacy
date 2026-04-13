package com.previred.jaguilar.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);

    private LogUtil() {
    }

    public static void info(String mensaje) {
        LOGGER.info(mensaje);
    }

    public static void warning(String mensaje) {
        LOGGER.warn(mensaje);
    }

    public static void error(String mensaje, Throwable e) {
        LOGGER.error(mensaje, e);
    }
}