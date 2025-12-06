    package pl.endixon.sectors.common.redis;

    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;

    public class MongoExecutor {
        public static final int THREADS = 8;
        public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREADS);

        public static void shutdown() {
            EXECUTOR.shutdown();
        }
    }
