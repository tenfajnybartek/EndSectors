/*
 *
 *  EndSectors  Non-Commercial License
 *  (c) 2025 Endixon
 *
 *  Permission is granted to use, copy, and
 *  modify this software **only** for personal
 *  or educational purposes.
 *
 *   Commercial use, redistribution, claiming
 *  this work as your own, or copying code
 *  without explicit permission is strictly
 *  prohibited.
 *
 *  Visit https://github.com/Endixon/EndSectors
 *  for more info.
 *
 */

package pl.endixon.sectors.proxy.user.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import pl.endixon.sectors.common.sector.SectorData;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;
import pl.endixon.sectors.proxy.manager.SectorManager;
import pl.endixon.sectors.proxy.util.CpuUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ProxyPingListener {

    private static final MiniMessage MM = MiniMessage.miniMessage();

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
                        "<gradient:#e2ebf0:#cfd9df>Innowacyjna architektura sektorowa</gradient> <dark_gray>»</dark_gray> <gradient:#00b09b:#96c93d>ONLINE</gradient>"
        );

        final List<ServerPing.SamplePlayer> hover = new ArrayList<>();

        final double cpuLoad = CpuUtil.getCpuLoad();
        final String formattedCpu = String.format("%.1f%%", cpuLoad);

        hover.add(this.createSample("<gradient:#00d2ff:#3a7bd5><bold>ENDSECTORS FRAMEWORK REVEAL</bold></gradient>"));
        hover.add(this.createSample("<gradient:#84fab0:#8fd3f4>Status systemu: TESTOWY</gradient>"));
        hover.add(this.createSample(""));

        hover.add(this.createSample("<gradient:#f6d365:#fda085>Aktywne instancje sektorowe: " + activeSectors + "</gradient>"));
        hover.add(this.createSample("<gradient:#a1c4fd:#c2e9fb>Użytkownicy na proxy: " + proxyOnline + "</gradient>"));

        final String cpuGradient = cpuLoad > 70.0 ? "<gradient:#ff0844:#ffb199>" : "<gradient:#fa709a:#fee140>";
        hover.add(this.createSample("<gradient:#e2ebf0:#cfd9df>Obciążenie CPU: </gradient>" + cpuGradient + formattedCpu + "</gradient>"));

        hover.add(this.createSample(""));
        hover.add(this.createSample("<gradient:#00d2ff:#3a7bd5:#00d2ff><bold>» KLIKNIJ ABY PRZETESTOWAĆ SILNIK «</bold></gradient>"));

        final ServerPing.Builder builder = ping.asBuilder()
                .description(motd)
                .onlinePlayers(proxyOnline)
                .maximumPlayers(totalMax)
                .samplePlayers(hover.toArray(new ServerPing.SamplePlayer[0]));

        event.setPing(builder.build());
    }

    private ServerPing.SamplePlayer createSample(final String content) {
        return new ServerPing.SamplePlayer(
                MM.serialize(MM.deserialize(content)),
                UUID.randomUUID()
        );
    }
}