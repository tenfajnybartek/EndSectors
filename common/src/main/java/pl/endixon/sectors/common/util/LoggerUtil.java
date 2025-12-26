package pl.endixon.sectors.common.util;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public final class LoggerUtil {

    private static final Logger logger = LoggerFactory.getLogger("EndSectors-Common");

    private LoggerUtil() {}


    public static void info(String message) {
        logger.info(message);
    }

    public static void info(Supplier<String> supplier) {
        if (logger.isInfoEnabled()) {
            logger.info(supplier.get());
        }
    }

    public static void info(String message, Object... args) {
        logger.info(message, args);
    }

    public static void info(String message, Throwable throwable) {
        logger.info(message, throwable);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void debug(Supplier<String> supplier) {
        if (logger.isDebugEnabled()) {
            logger.debug(supplier.get());
        }
    }

    public static void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    public static void debug(String message, Throwable throwable) {
        logger.debug(message, throwable);
    }
}
