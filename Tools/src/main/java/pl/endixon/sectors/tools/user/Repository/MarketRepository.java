    package pl.endixon.sectors.tools.user.Repository;

    import static com.mongodb.client.model.Filters.eq;
    import com.mongodb.client.MongoCollection;
    import lombok.RequiredArgsConstructor;
    import pl.endixon.sectors.common.Common;
    import pl.endixon.sectors.common.packet.PacketChannel;
    import pl.endixon.sectors.tools.nats.packet.PacketMarketNotify;
    import pl.endixon.sectors.tools.nats.packet.PacketMarketUpdate;
    import pl.endixon.sectors.tools.user.profile.PlayerProfile;
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
            collection.replaceOne(eq("_id", offer.getId()), offer,
                    new com.mongodb.client.model.ReplaceOptions().upsert(true));
            ProfileMarketCache.put(offer);
        }

        public List<PlayerMarketProfile> findByCategory(String category) {
            return ProfileMarketCache.getValues().stream()
                    .filter(offer -> offer.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        public void listOffer(PlayerProfile seller, String itemData, String itemName, String category, double price) {
            UUID offerId = UUID.randomUUID();
            long now = System.currentTimeMillis();

            PlayerMarketProfile offer = new PlayerMarketProfile(
                    offerId,
                    seller.getUuid(),
                    seller.getName(),
                    itemData,
                    itemName,
                    category,
                    price,
                    now
            );
            this.save(offer);

            PacketMarketUpdate packet = new PacketMarketUpdate(
                    offerId,
                    "ADD",
                    seller.getUuid(),
                    seller.getName(),
                    itemData,
                    itemName,
                    category,
                    price,
                    now
            );
            Common.getInstance().getNatsManager().publish(PacketChannel.MARKET_UPDATE.getSubject(), packet);
        }

        public boolean remove(UUID id) {
            if (collection.deleteOne(eq("_id", id)).getDeletedCount() > 0) {
                ProfileMarketCache.remove(id);
                Common.getInstance().getNatsManager().publish(
                        PacketChannel.MARKET_UPDATE.getSubject(),
                        PacketMarketUpdate.remove(id)
                );
                return true;
            }
            return false;
        }


        public List<PlayerMarketProfile> findBySeller(UUID sellerUuid) {
            return ProfileMarketCache.getValues().stream()
                    .filter(offer -> offer.getSellerUuid().equals(sellerUuid))
                    .toList();
        }

        public boolean cancelOffer(UUID offerId, UUID sellerUuid) {
            return this.find(offerId).map(offer -> {
                if (!offer.getSellerUuid().equals(sellerUuid)) {
                    return false;
                }
                return this.remove(offerId);
            }).orElse(false);
        }


        public PurchaseResult buyOffer(UUID offerId, PlayerProfile buyer, PlayerRepository playerRepository) {
            return this.find(offerId).map(offer -> {
                if (buyer.getBalance() < offer.getPrice()) {
                    return PurchaseResult.NOT_ENOUGH_MONEY;
                }

                if (!this.remove(offerId)) {
                    return PurchaseResult.ALREADY_SOLD;
                }

                buyer.setBalance(buyer.getBalance() - offer.getPrice());
                playerRepository.save(buyer);


                playerRepository.find(offer.getSellerUuid()).ifPresent(seller -> {
                    seller.setBalance(seller.getBalance() + offer.getPrice());
                    playerRepository.save(seller);
                });

                PacketMarketNotify notify = new PacketMarketNotify(
                        offer.getSellerUuid(),
                        buyer.getName(),
                        offer.getItemName(),
                        offer.getPrice()
                );
                Common.getInstance().getNatsManager().publish(PacketChannel.MARKET_NOTIFY.getSubject(), notify);

                return PurchaseResult.SUCCESS;
            }).orElse(PurchaseResult.NOT_FOUND);
        }

        public enum PurchaseResult {
            SUCCESS, NOT_ENOUGH_MONEY, ALREADY_SOLD, NOT_FOUND
        }
    }