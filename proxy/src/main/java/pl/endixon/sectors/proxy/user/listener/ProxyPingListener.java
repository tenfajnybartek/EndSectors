package pl.endixon.sectors.proxy.user.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import pl.endixon.sectors.common.sector.SectorData;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;
import pl.endixon.sectors.proxy.manager.SectorManager;
import pl.endixon.sectors.proxy.util.CpuUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Enterprise Proxy Ping Listener.
 * Optimized for high-performance hover formatting.
 */
public class ProxyPingListener {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    // Używamy LegacySerializer, bo SamplePlayer w protokole Minecrafta najlepiej radzi sobie z §
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private final ProxyServer proxyServer = VelocitySectorPlugin.getInstance().getServer();
    private final SectorManager sectorManager = VelocitySectorPlugin.getInstance().getSectorManager();

    @Subscribe
    public void onProxyPing(final ProxyPingEvent event) {
        final ServerPing ping = event.getPing();

        final int proxyOnline = this.proxyServer.getPlayerCount();
        int totalMax = 0;
        int activeSectors = 0;

        for (final SectorData sector : this.sectorManager.getSectorsData()) {
            if (sector.isOnline()) {
                totalMax += sector.getMaxPlayers();
                activeSectors++;
            }
        }

        final Component motd = MM.deserialize(
                "<gradient:#00d2ff:#3a7bd5><bold>ENDSECTORS</bold></gradient> <white>•</white> <gradient:#f6d365:#fda085>FRAMEWORK PREVIEW</gradient>\n" +
                        "<gradient:#e2ebf0:#cfd9df>Innowacyjny system sektorow</gradient> <dark_gray>»</dark_gray> <gradient:#00b09b:#96c93d>ONLINE</gradient>"
        );

        final List<ServerPing.SamplePlayer> hover = new ArrayList<>();
        final double cpuLoad = CpuUtil.getCpuLoad();
        final String formattedCpu = String.format("%.1f%%", cpuLoad);

        hover.add(this.createSample("<gradient:#00d2ff:#3a7bd5><bold>ENDSECTORS FRAMEWORK REVEAL</bold></gradient>"));
        hover.add(this.createSample("<gradient:#84fab0:#8fd3f4>Status systemu: TESTOWY</gradient>"));
        hover.add(this.createSample(""));
        hover.add(this.createSample("<gradient:#f6d365:#fda085>Aktywne sektory: " + activeSectors + "</gradient>"));
        hover.add(this.createSample("<gradient:#a1c4fd:#c2e9fb>Użytkownicy na proxy: " + proxyOnline + "</gradient>"));

        final String cpuGradient = cpuLoad > 70.0 ? "<gradient:#ff0844:#ffb199>" : "<gradient:#fa709a:#fee140>";
        hover.add(this.createSample("<gradient:#e2ebf0:#cfd9df>Obciążenie CPU: </gradient>" + cpuGradient + formattedCpu + "</gradient>"));

        hover.add(this.createSample(""));
        hover.add(this.createSample("<gradient:#00d2ff:#3a7bd5:#00d2ff><bold>» KLIKNIJ ABY PRZETESTOWAĆ SYSTEM «</bold></gradient>"));

        final ServerPing.Builder builder = ping.asBuilder()
                .description(motd)
                .onlinePlayers(proxyOnline)
                .maximumPlayers(totalMax)
                .samplePlayers(hover.toArray(new ServerPing.SamplePlayer[0]));

        event.setPing(builder.build());
    }

    private ServerPing.SamplePlayer createSample(final String content) {
        if (content.isEmpty()) {
            return new ServerPing.SamplePlayer("", UUID.randomUUID());
        }
        final Component component = MM.deserialize(content);
        final String legacyContent = LEGACY.serialize(component);
        return new ServerPing.SamplePlayer(legacyContent, UUID.randomUUID());
    }
}