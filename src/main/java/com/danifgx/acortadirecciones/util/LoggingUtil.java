package com.danifgx.acortadirecciones.util;

import org.slf4j.Logger;

public class LoggingUtil {
    public static void logCurrentMethod(Logger logger) {
        StackTraceElement currentElement = getCurrentStackTraceElement();
        String className = currentElement.getClassName();
        String methodName = currentElement.getMethodName();
        logger.debug("Executing method: {} of class: {}", methodName, className);
    }

    public static StackTraceElement getCurrentStackTraceElement() {
        return Thread.currentThread().getStackTrace()[2];
    }
}
