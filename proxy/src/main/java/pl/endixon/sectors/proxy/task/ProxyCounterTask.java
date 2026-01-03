package pl.endixon.sectors.proxy.task;

import pl.endixon.sectors.common.Common;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;
import java.util.concurrent.atomic.AtomicInteger;

public final class ProxyCounterTask implements Runnable {

    private static final AtomicInteger GLOBAL_ONLINE = new AtomicInteger(0);
    private final VelocitySectorPlugin plugin = VelocitySectorPlugin.getInstance();

    @Override
    public void run() {
        String proxyId = plugin.getConfigLoader().proxyName;
        int localCount = plugin.getServer().getPlayerCount();
        Common.getInstance().getRedisManager().updateProxyCountAsync(proxyId, localCount);
        Common.getInstance().getRedisManager().getGlobalOnlineCountAsync().thenAccept(GLOBAL_ONLINE::set);
    }

    public static int getGlobalOnline() {
        return GLOBAL_ONLINE.get();
    }
}