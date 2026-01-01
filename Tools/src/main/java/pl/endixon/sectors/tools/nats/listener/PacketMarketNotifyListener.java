package pl.endixon.sectors.tools.nats.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.tools.nats.packet.PacketMarketNotify;

public class PacketMarketNotifyListener implements PacketListener<PacketMarketNotify> {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public void handle(PacketMarketNotify packet) {
        Player seller = Bukkit.getPlayer(packet.getSellerUuid());

        if (seller != null && seller.isOnline()) {

            String messageFormat =
                    "<dark_gray>[<gradient:#ffaa00:#ffff55>Market</gradient><dark_gray>] " +
                            "<gray>Gracz <yellow>" + packet.getBuyerName() +
                            " <gray>kupił Twój przedmiot <#dddddd>" + packet.getItemName() +
                            " <gray>za <gradient:#55ff55:#00aa00><bold>" + packet.getPrice() + "$</bold></gradient>";

            Component component = MM.deserialize(messageFormat);
            seller.sendMessage(component);
            seller.playSound(seller.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        }
    }
}