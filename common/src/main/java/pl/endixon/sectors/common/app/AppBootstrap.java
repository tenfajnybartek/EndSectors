/*
 *
 * EndSectors – Non-Commercial License
 * (c) 2025 Endixon
 *
 * Permission is granted to use, copy, and
 * modify this software **only** for personal
 * or educational purposes.
 *
 * Commercial use, redistribution, claiming
 * this work as your own, or copying code
 * without explicit permission is strictly
 * prohibited.
 *
 * Visit https://github.com/Endixon/EndSectors
 * for more info.
 *
 */

package pl.endixon.sectors.common.app;

import pl.endixon.sectors.common.Common;
import pl.endixon.sectors.common.packet.object.*;
import pl.endixon.sectors.common.util.AppLogger;

public final class AppBootstrap {

    public static void main(String[] args) {
        Common.initInstance();
        Common app = Common.getInstance();
        AppLogger logger = app.getLogger();
        logger.info("  ");
        logger.info("========================================");
        logger.info("    EndSectors - Common App Service     ");
        logger.info("           Status: STARTING             ");
        logger.info("========================================");
        logger.info("  ");

        try {
            app.setAppBootstrap(true);
            logger.info(">> [1/3] Connecting to NATS Infrastructure...");
            logger.info("  ");
            app.initializeNats("nats://127.0.0.1:4222", "common-app");

            logger.info("  ");
            logger.info(">> [2/3] Activating Sniffer Responder...");
            logger.info("  ");
            app.getFlowLogger().enable(true);

            logger.info("  ");
            logger.info(">> [3/3] Activating Heartbeat Responder...");
            logger.info("  ");
            app.startHeartbeat();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("  ");
                logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                logger.warn("   SHUTDOWN SIGNAL - CLEANING UP...     ");

                if (app.getHeartbeat() != null) {
                    app.getHeartbeat().stop();
                }

                app.shutdown();
                logger.info("   Safe shutdown complete. Goodbye!     ");
                logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                logger.info("  ");
            }, "Common-Shutdown-Thread"));

            logger.info("  ");
            logger.info("----------------------------------------");
            logger.info(">>> Common App is READY and LISTENING   ");
            logger.info(">>> System is stable and operational.   ");
            logger.info("----------------------------------------");
            logger.info("  ");

            Thread.currentThread().join();

        } catch (Exception exception) {
            logger.info("  ");
            logger.error("========================================");
            logger.error(" FATAL ERROR: Initialization failed!    ");
            logger.error(" Message: " + exception.getMessage());
            logger.error("========================================");
            logger.info("  ");

            if (app.getHeartbeat() != null) {
                app.getHeartbeat().broadcastEmergencyStop(exception.getMessage());
            }
            exception.printStackTrace();
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}

            System.exit(1);
        }
    }



}
