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

package pl.endixon.sectors.paper.manager;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.endixon.sectors.common.sector.SectorData;
import pl.endixon.sectors.common.sector.SectorType;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.user.profile.UserProfile;
import pl.endixon.sectors.paper.user.profile.UserProfileCache;
import pl.endixon.sectors.paper.util.LoggerUtil;

@RequiredArgsConstructor
public class SectorManager {

    private final Map<String, Sector> sectors = new HashMap<>();
    private final PaperSector paperSector;

    @Getter
    private final String currentSectorName;

    public void addSector(SectorData sectorData) {
        this.addSector(new Sector(sectorData));
    }

    public void addSector(Sector sector) {
        this.sectors.put(sector.getName(), sector);
    }

    public void loadSectorsData(SectorData[] sectorsData) {
        for (SectorData sectorData : sectorsData) {
            this.addSector(sectorData);
        }
    }

    public Sector getSector(String sectorName) {
        return this.sectors.get(sectorName);
    }

    public Sector getSector(Location location) {
        for (Sector sector : sectors.values()) {
            if (sector.isInSector(location)) {
                if (sector.getType() == SectorType.QUEUE)
                    continue;
                if (getCurrentSector() == null || getCurrentSector().getType() != SectorType.SPAWN) {
                    return sector;
                } else if (sector.getType() != SectorType.SPAWN) {
                    return sector;
                }
            }
        }
        return null;
    }

    public Sector find(SectorType type) {
        return this.sectors.values().stream()
                .filter(Sector::isOnline)
                .filter(s -> s.getType() == type)
                .filter(s -> s.getType() != SectorType.END)
                .filter(s -> s.getType() != SectorType.NETHER)
                .filter(s -> s.getType() != SectorType.QUEUE)
                .findFirst()
                .orElse(null);
    }

    public Sector getRandomSector() {
        List<Sector> candidates = sectors.values().stream()
                .filter(Sector::isOnline)
                .filter(s -> s.getType() != SectorType.NETHER)
                .filter(s -> s.getType() != SectorType.END)
                .filter(s -> s.getType() != SectorType.QUEUE)
                .filter(s -> s.getType() != SectorType.SPAWN)
                .toList();

        if (candidates.isEmpty()) {
            throw new IllegalStateException("Brak dostępnych sektorów do losowania!");
        }

        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }


    public Location randomLocation(@NonNull Player player, @NonNull UserProfile user) {
        Sector randomSector = getRandomSector();

        World world = Bukkit.getWorld(randomSector.getWorldName());
        if (world == null) {
            throw new IllegalStateException("World not loaded for sector " + randomSector.getName());
        }

        double safeMargin = 20.0;
        double minX = Math.min(randomSector.getFirstCorner().getPosX(), randomSector.getSecondCorner().getPosX()) + safeMargin;
        double maxX = Math.max(randomSector.getFirstCorner().getPosX(), randomSector.getSecondCorner().getPosX()) - safeMargin;
        double minZ = Math.min(randomSector.getFirstCorner().getPosZ(), randomSector.getSecondCorner().getPosZ()) + safeMargin;
        double maxZ = Math.max(randomSector.getFirstCorner().getPosZ(), randomSector.getSecondCorner().getPosZ()) - safeMargin;
        double x = minX + ThreadLocalRandom.current().nextDouble(maxX - minX);
        double z = minZ + ThreadLocalRandom.current().nextDouble(maxZ - minZ);
        int y = findSafeY(world, (int) x, (int) z);

        Location loc = new Location(world, x, y, z);

        user.setLocationAndSave(loc);

        if (randomSector.getName().equals(user.getSectorName())) {
            player.teleport(loc);
            user.updateAndSave(player, randomSector);
        } else {
            paperSector.getSectorTeleport().teleportToSector(player, user, randomSector, false, true);
        }
        return loc;
    }

    private int findSafeY(World world, int x, int z) {
        int maxHeight = world.getMaxHeight() - 2;
        for (int y = maxHeight; y > 0; y--) {
            Block block = world.getBlockAt(x, y, z);
            Block above = world.getBlockAt(x, y + 1, z);
            Block above2 = world.getBlockAt(x, y + 2, z);
            if (isSafeBase(block) && above.isEmpty() && above2.isEmpty()) {
                return y + 1;
            }
        }
        return world.getHighestBlockYAt(x, z) + 1;
    }

    private boolean isSafeBase(Block block) {
        Material type = block.getType();
        return type == Material.GRASS_BLOCK || type == Material.DIRT || type == Material.SAND;
    }

    public Sector getBalancedRandomSpawnSector() {

        List<Sector> allSpawns = sectors.values().stream()
                .filter(s -> s.getType() == SectorType.SPAWN)
                .toList();

        List<Sector> healthySpawns = new ArrayList<>(
                allSpawns.stream()
                        .filter(Sector::isOnline)
                        .filter(s -> s.getTPS() > 15.0)
                        .toList()
        );

        LoggerUtil.info(String.format(
                "All spawns: %d | Healthy spawns: %d",
                allSpawns.size(), healthySpawns.size()
        ));

        if (healthySpawns.isEmpty()) {
            LoggerUtil.info(String.format(
                    "Balance error! All spawns (%d) are either offline or lagging!",
                    allSpawns.size()
            ));
            return null;
        }


        Collections.shuffle(healthySpawns);

        healthySpawns.sort(Comparator.comparingDouble(s -> {
            double occupancy = (double) s.getPlayerCount() / Math.max(s.getMaxPlayers(), 1);
            return occupancy / s.getTPS();
        }));

        int poolSize = Math.max(1, (int) Math.ceil(healthySpawns.size() * 0.3));

        LoggerUtil.info(String.format(
                "Spawn balance: Online: %d/%d | Random selection from top %d%% (~%d spawns)",
                healthySpawns.size(), allSpawns.size(), 30, poolSize
        ));

        Sector chosen = healthySpawns.get(
                ThreadLocalRandom.current().nextInt(poolSize)
        );

        LoggerUtil.info(String.format(
                "Chosen spawn: %s | Players: %d/%d | TPS: %.2f",
                chosen.getName(),
                chosen.getPlayerCount(),
                chosen.getMaxPlayers(),
                chosen.getTPS()
        ));

        return chosen;
    }


    public Sector getCurrentSector() {
        return this.getSector(currentSectorName);
    }

    public Collection<Sector> getSectors() {
        return this.sectors.values();
    }

    public void getOnlinePlayers(Consumer<List<String>> callback) {
        paperSector.getRedisManager().getOnlinePlayers(callback);
    }

    public void isPlayerOnline(String playerName, Consumer<Boolean> callback) {
        paperSector.getRedisManager().isPlayerOnline(playerName, callback);
    }
}
