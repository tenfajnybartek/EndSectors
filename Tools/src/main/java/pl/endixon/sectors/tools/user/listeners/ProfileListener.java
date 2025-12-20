    package pl.endixon.sectors.tools.user.listeners;

    import lombok.RequiredArgsConstructor;
    import org.bukkit.entity.Player;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;
    import org.bukkit.event.player.PlayerJoinEvent;
    import org.bukkit.event.player.PlayerQuitEvent;
    import pl.endixon.sectors.tools.user.profile.ProfileCache;
    import pl.endixon.sectors.tools.user.profile.PlayerProfileRepository;
    import pl.endixon.sectors.tools.user.profile.PlayerProfile;

    import java.util.UUID;

    @RequiredArgsConstructor
    public class ProfileListener implements Listener {

        private final PlayerProfileRepository repository;

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();

            PlayerProfile profile = repository.find(player.getUniqueId())
                    .orElseGet(() ->
                            repository.create(player.getUniqueId(), player.getName())
                    );
            ProfileCache.put(profile);
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            UUID uuid = event.getPlayer().getUniqueId();
            PlayerProfile profile = ProfileCache.get(uuid);

            if (profile != null) {
                repository.save(profile);
                ProfileCache.remove(uuid);
            }
        }
    }
