package pl.endixon.sectors.common.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public final class LoggerUtil {

    private static final String PREFIX = "EndSectors-Common";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LoggerUtil() {}

    private static String timestamp() {
        return LocalTime.now().format(TIME_FORMAT);
    }

    public static void info(String message) {
        System.out.printf("[%s] [INFO] %s - %s%n", timestamp(), PREFIX, message);
    }

    public static void warn(String message) {
        System.out.printf("[%s] [WARN] %s - %s%n", timestamp(), PREFIX, message);
    }

    public static void error(String message) {
        System.err.printf("[%s] [ERROR] %s - %s%n", timestamp(), PREFIX, message);
    }

    public static void debug(String message) {
        System.out.printf("[%s] [DEBUG] %s - %s%n", timestamp(), PREFIX, message);
    }

    public static void info(Supplier<String> supplier) {
        System.out.printf("[%s] [INFO]  %s - %s%n", timestamp(), PREFIX, supplier.get());
    }

    public static void debug(Supplier<String> supplier) {
        System.out.printf("[%s] [DEBUG] %s - %s%n", timestamp(), PREFIX, supplier.get());
    }

    public static void info(String message, Object... args) {
        System.out.printf("[%s] [INFO] %s - %s%n", timestamp(), PREFIX, String.format(message, args));
    }

    public static void error(String message, Throwable throwable) {
        System.err.printf("[%s] [ERROR] %s - %s%n", timestamp(), PREFIX, message);
        throwable.printStackTrace(System.err);
    }

    public static void info(String message, Throwable throwable) {
        System.out.printf("[%s] [INFO] %s - %s%n", timestamp(), PREFIX, message);
        throwable.printStackTrace(System.out);
    }
}
