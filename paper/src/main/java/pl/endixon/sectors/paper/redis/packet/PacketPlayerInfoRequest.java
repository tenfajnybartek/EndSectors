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
    package pl.endixon.sectors.paper.redis.packet;

    import com.fasterxml.jackson.annotation.JsonCreator;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import lombok.Getter;
    import pl.endixon.sectors.common.packet.Packet;

    import java.util.List;

    @Getter
    public class PacketPlayerInfoRequest extends Packet {

        private final List<PlayerData> players;

        @JsonCreator
        public PacketPlayerInfoRequest(
                @JsonProperty("players") List<PlayerData> players
        ) {
            this.players = players;
        }

        @Getter
        public static class PlayerData {
            private final String name;
            private final String sector;
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

            @JsonCreator
            public PlayerData(
                    @JsonProperty("name") String name,
                    @JsonProperty("sector") String sector,
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
                    @JsonProperty("playerGameMode") String playerGameMode
            ) {
                this.name = name;
                this.sector = sector;
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
            }

  
        }
    }

