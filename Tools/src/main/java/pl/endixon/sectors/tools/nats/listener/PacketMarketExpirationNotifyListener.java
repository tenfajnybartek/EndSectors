package pl.endixon.sectors.tools.nats.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.tools.nats.packet.PacketMarketExpirationNotify;

public class PacketMarketExpirationNotifyListener implements PacketListener<PacketMarketExpirationNotify> {


    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public void handle(PacketMarketExpirationNotify packet) {
        Player player = Bukkit.getPlayer(packet.getOwnerUuid());

        if (player != null && player.isOnline()) {

            Component header = MM.deserialize("<newline><dark_gray><bold>» <gradient:#ffaa00:#ffff55>MARKET</gradient> <dark_gray><bold>«");
            Component line1 = MM.deserialize("<dark_gray>| <#ff5555>Twoje przedmioty wygasły i trafiły do Magazynu.");
            Component line2 = MM.deserialize("<dark_gray>| <gray>Liczba przedmiotów w magazynie: <yellow>" + packet.getExpiredCount());
            Component line3 = MM.deserialize("<dark_gray>| <gray>Kliknij <yellow><click:run_command:'/market'><hover:show_text:'<gray>Kliknij, aby otworzyć rynek'><bold>/market</bold></hover></click><gray>, aby je odebrać.<newline>");
            player.sendMessage(header);
            player.sendMessage(line1);
            player.sendMessage(line2);
            player.sendMessage(line3);
            player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
        }
    }
}