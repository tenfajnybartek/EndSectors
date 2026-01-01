package pl.endixon.sectors.tools.user.profile;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ProfileMarketCache {

    private static final Map<UUID, PlayerMarketProfile> CACHE = new ConcurrentHashMap<>();

    public static PlayerMarketProfile get(UUID id) {
        return CACHE.get(id);
    }

    public static void put(PlayerMarketProfile offer) {
        CACHE.put(offer.getId(), offer);
    }

    public static void remove(UUID id) {
        CACHE.remove(id);
    }

    public static Collection<PlayerMarketProfile> getValues() {
        return CACHE.values();
    }

    public static List<PlayerMarketProfile> getByCategory(String category) {
        return CACHE.values().stream()
                .filter(offer -> offer.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public static List<PlayerMarketProfile> getBySeller(UUID sellerUuid) {
        return CACHE.values().stream()
                .filter(offer -> offer.getSellerUuid().equals(sellerUuid))
                .collect(Collectors.toList());
    }

    public static void clear() {
        CACHE.clear();
    }
}