package pl.endixon.sectors.tools.nats.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.tools.nats.packet.PacketMarketNotify;
import pl.endixon.sectors.tools.user.profile.PlayerProfile;
import pl.endixon.sectors.tools.user.profile.ProfileCache;

public class PacketMarketNotifyListener implements PacketListener<PacketMarketNotify> {

    @Override
    public void handle(PacketMarketNotify packet) {
        Player player = Bukkit.getPlayer(packet.getSellerUuid());
        PlayerProfile profile = ProfileCache.get(packet.getSellerUuid());

        if (profile != null) {
            profile.setBalance(profile.getBalance() + packet.getPrice());
        }

        if (player != null && player.isOnline()) {
            player.sendMessage(" ");
            player.sendMessage("§8[§6Rynek§8] §aTwój przedmiot został sprzedany!");
            player.sendMessage("§8» §7Przedmiot: §f" + packet.getItemName());
            player.sendMessage("§8» §7Kupujący: §f" + packet.getBuyerName());
            player.sendMessage("§8» §7Zarobek: §e" + packet.getPrice() + "$");
            player.sendMessage(" ");
        }
    }
}