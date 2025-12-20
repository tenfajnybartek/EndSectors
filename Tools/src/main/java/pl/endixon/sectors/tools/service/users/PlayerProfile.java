package pl.endixon.sectors.tools.service.users;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import pl.endixon.sectors.tools.service.home.Home;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfile {

    private UUID uuid;
    private String name;
    private int kills;
    private int deaths;
    private Map<String, Home> homes = new HashMap<>();
    private long combatUntil;

    public void startCombat(long durationSeconds) {
        this.combatUntil = System.currentTimeMillis() + durationSeconds * 1000;
    }

    public boolean isInCombat() {
        return System.currentTimeMillis() < combatUntil;
    }

    public long getCombatRemainingMillis() {
        return Math.max(0, combatUntil - System.currentTimeMillis());
    }

    public long getCombatRemainingSeconds() {
        return getCombatRemainingMillis() / 1000;
    }
}
