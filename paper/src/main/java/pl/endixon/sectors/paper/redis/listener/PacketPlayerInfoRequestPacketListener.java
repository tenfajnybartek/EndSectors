package pl.endixon.sectors.paper.redis.listener;

import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.paper.redis.packet.PacketPlayerInfoRequest;
import pl.endixon.sectors.paper.user.UserManager;

public class PacketPlayerInfoRequestPacketListener extends RedisPacketListener<PacketPlayerInfoRequest> {

    public PacketPlayerInfoRequestPacketListener() {
        super(PacketPlayerInfoRequest.class);
    }

    @Override
    public void handle(PacketPlayerInfoRequest packet) {
        if (packet.getPlayers().isEmpty()) return;

        packet.getPlayers().forEach(playerData -> {
            UserManager.getUser(playerData.getName()).thenAccept(user -> {
                if (user != null) {
                    user.setFoodLevel(playerData.getFoodLevel());
                    user.setExperience(playerData.getExperience());
                    user.setExperienceLevel(playerData.getExperienceLevel());
                    user.setFireTicks(playerData.getFireTicks());
                    user.setAllowFlight(playerData.isAllowFlight());
                    user.setFlying(playerData.isFlying());
                    user.setPlayerGameMode(playerData.getPlayerGameMode());
                }
            });
        });
    }
}
