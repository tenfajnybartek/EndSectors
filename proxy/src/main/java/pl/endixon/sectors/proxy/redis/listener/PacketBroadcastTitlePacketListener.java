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

package pl.endixon.sectors.proxy.redis.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.common.packet.object.PacketBroadcastTitle;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;

import java.time.Duration;

public class PacketBroadcastTitlePacketListener implements PacketListener<PacketBroadcastTitle> {

    @Override
    public void handle(final PacketBroadcastTitle packet) {
        final ProxyServer server = VelocitySectorPlugin.getInstance().getServer();
        if (server == null) {
            return;
        }

        final Component title = Component.text(packet.getTitle());
        final Component subtitle = Component.text(packet.getSubTitle());

        final Title.Times times = Title.Times.times(
                Duration.ofMillis(packet.getFadeIn()),
                Duration.ofMillis(packet.getStay()),
                Duration.ofMillis(packet.getFadeOut())
        );

        final Title adventureTitle = Title.title(title, subtitle, times);

        for (final Player player : server.getAllPlayers()) {
            player.showTitle(adventureTitle);
        }
    }
}