package pl.endixon.sectors.paper.redis.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pl.endixon.sectors.common.packet.Packet;
import pl.endixon.sectors.paper.user.UserMongo;

@Getter
public class PacketPlayerInfoRequest extends Packet {

    private final String name;
    private final String sectorName;
    private final boolean firstJoin;
    private final long lastSectorTransfer;
    private final long lastTransferTimestamp;
    private final boolean teleportingToSector;
    private final int foodLevel;
    private final int experience;
    private final int experienceLevel;
    private final int fireTicks;
    private final boolean allowFlight;
    private final boolean flying;
    private final String playerGameMode;
    private final String playerInventoryData;
    private final String playerEnderChestData;
    private final String playerEffectsData;

    public PacketPlayerInfoRequest(UserMongo user) {
        this(
                user.getName(),
                user.getSectorName(),
                user.isFirstJoin(),
                user.getLastSectorTransfer(),
                user.getLastTransferTimestamp(),
                user.isTeleportingToSector(),
                user.getFoodLevel(),
                user.getExperience(),
                user.getExperienceLevel(),
                user.getFireTicks(),
                user.isAllowFlight(),
                user.isFlying(),
                user.getPlayerGameMode(),
                user.getPlayerInventoryData(),
                user.getPlayerEnderChestData(),
                user.getPlayerEffectsData()
        );
    }

    @JsonCreator
    public PacketPlayerInfoRequest(
            @JsonProperty("name") String name,
            @JsonProperty("sectorName") String sectorName,
            @JsonProperty("firstJoin") boolean firstJoin,
            @JsonProperty("lastSectorTransfer") long lastSectorTransfer,
            @JsonProperty("lastTransferTimestamp") long lastTransferTimestamp,
            @JsonProperty("teleportingToSector") boolean teleportingToSector,
            @JsonProperty("foodLevel") int foodLevel,
            @JsonProperty("experience") int experience,
            @JsonProperty("experienceLevel") int experienceLevel,
            @JsonProperty("fireTicks") int fireTicks,
            @JsonProperty("allowFlight") boolean allowFlight,
            @JsonProperty("flying") boolean flying,
            @JsonProperty("playerGameMode") String playerGameMode,
            @JsonProperty("playerInventoryData") String playerInventoryData,
            @JsonProperty("playerEnderChestData") String playerEnderChestData,
            @JsonProperty("playerEffectsData") String playerEffectsData
    ) {
        this.name = name;
        this.sectorName = sectorName;
        this.firstJoin = firstJoin;
        this.lastSectorTransfer = lastSectorTransfer;
        this.lastTransferTimestamp = lastTransferTimestamp;
        this.teleportingToSector = teleportingToSector;
        this.foodLevel = foodLevel;
        this.experience = experience;
        this.experienceLevel = experienceLevel;
        this.fireTicks = fireTicks;
        this.allowFlight = allowFlight;
        this.flying = flying;
        this.playerGameMode = playerGameMode;
        this.playerInventoryData = playerInventoryData;
        this.playerEnderChestData = playerEnderChestData;
        this.playerEffectsData = playerEffectsData;
    }
}
