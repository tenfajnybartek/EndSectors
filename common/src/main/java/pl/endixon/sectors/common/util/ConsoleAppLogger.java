package pl.endixon.sectors.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConsoleAppLogger implements AppLogger {

    private final Logger logger;

    public ConsoleAppLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    @Override
    public void info(String message) {
        logger.info(message);
        logToConsole("INFO", message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
        logToConsole("WARN", message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
        logToConsole("ERROR", message);
    }

    private void logToConsole(String level, String msg) {
        java.time.format.DateTimeFormatter timeFormat = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
        String timestamp = java.time.LocalTime.now().format(timeFormat);
        System.out.printf("[%s] [%s] %s - %s%n", timestamp, level, logger.getName(), msg);
    }
}
