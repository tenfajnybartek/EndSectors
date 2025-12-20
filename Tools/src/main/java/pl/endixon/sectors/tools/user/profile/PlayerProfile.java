package pl.endixon.sectors.tools.user.profile;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfile {
    @BsonId
    private UUID uuid;
    private String name;
    private int kills;
    private int deaths;
    private Map<String, ProfileHome> homes = new HashMap<>();
    private long combatUntil;
}
