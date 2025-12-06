package pl.endixon.sectors.common.util;

public class Logger {

    public static void info(Object object) {
        System.out.println(ChatUtil.fixColorsLogger("%M[EndSectors-Common] %C" + object));
    }

    public static void warn(Object object) {
        System.out.println(ChatUtil.fixColorsLogger("%M[EndSectors-Common] §e" + object));
    }

    public static void error(Object object) {
        System.err.println(ChatUtil.fixColorsLogger("%M[EndSectors-Common] §c" + object));
    }

    public static void info(String message, Long count, String channel) {
        String formatted = String.format("%s [EndSectors-Common] %s (sent to %d subscribers on channel %s)", ChatUtil.fixColorsLogger("%M"), message, count, channel);
        System.out.println(formatted);
    }
}
