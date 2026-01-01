package pl.endixon.sectors.tools.market.repository;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import lombok.RequiredArgsConstructor;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import pl.endixon.sectors.tools.user.profile.ProfileMarketCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class MarketRepository {

    private final MongoCollection<PlayerMarketProfile> collection;

    public void warmup() {
        List<PlayerMarketProfile> allOffers = collection.find().into(new ArrayList<>());
        allOffers.forEach(ProfileMarketCache::put);
    }

    public Optional<PlayerMarketProfile> find(UUID id) {
        PlayerMarketProfile cached = ProfileMarketCache.get(id);
        if (cached != null) return Optional.of(cached);
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    public void save(PlayerMarketProfile offer) {
        collection.replaceOne(eq("_id", offer.getId()), offer, new ReplaceOptions().upsert(true));
        ProfileMarketCache.put(offer);
    }

    public boolean delete(UUID id) {
        if (collection.deleteOne(eq("_id", id)).getDeletedCount() > 0) {
            ProfileMarketCache.remove(id);
            return true;
        }
        return false;
    }

    public List<PlayerMarketProfile> findByCategory(String category) {
        return ProfileMarketCache.getValues().stream()
                .filter(offer -> offer.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    public List<PlayerMarketProfile> findBySeller(UUID sellerUuid) {
        return ProfileMarketCache.getValues().stream()
                .filter(offer -> offer.getSellerUuid().equals(sellerUuid))
                .toList();
    }
}