package pl.endixon.sectors.paper.user;

import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.util.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class RedisUserCache {

    private static final String PREFIX = "user:";

    private RedisUserCache() {}

    private static String getKey(String name) {
        return PREFIX + name.toLowerCase();
    }

    public static void save(UserRedis user) {
        runSafely(() -> {
            PaperSector.getInstance()
                    .getRedisService()
                    .hset(getKey(user.getName()), user.toRedisMap());
        }, () -> String.format("[RedisUserCache] Failed to save user '%s'", user.getName()));
    }

    public static Optional<Map<String, String>> load(String name) {
        return supplySafely(() -> {
            Map<String, String> data = PaperSector.getInstance()
                    .getRedisService()
                    .hgetAll(getKey(name));
            return data != null && !data.isEmpty() ? data : null;
        }, () -> String.format("[RedisUserCache] Failed to load user '%s'", name));
    }

    public static void delete(String name) {
        runSafely(() -> {
            PaperSector.getInstance()
                    .getRedisService()
                    .del(getKey(name));
        }, () -> String.format("[RedisUserCache] Failed to delete user '%s'", name));
    }

    private static void runSafely(Runnable action, Supplier<String> error) {
        try {
            action.run();
        } catch (Exception e) {
            Logger.info(String.format("%s (%s): %s", error.get(), e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    private static <T> Optional<T> supplySafely(Supplier<T> action, Supplier<String> error) {
        try {
            return Optional.ofNullable(action.get());
        } catch (Exception e) {
            Logger.info(String.format("%s (%s): %s", error.get(), e.getClass().getSimpleName(), e.getMessage()));
            return Optional.empty();
        }
    }
}
