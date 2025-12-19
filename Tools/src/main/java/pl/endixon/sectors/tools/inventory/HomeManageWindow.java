package pl.endixon.sectors.tools.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.endixon.sectors.common.util.ChatUtil;
import pl.endixon.sectors.tools.Main;
import pl.endixon.sectors.tools.inventory.api.WindowUI;
import pl.endixon.sectors.tools.inventory.api.builder.StackBuilder;
import pl.endixon.sectors.tools.service.home.Home;
import pl.endixon.sectors.tools.service.users.PlayerProfile;

import java.util.Collections;
import java.util.Map;

public class HomeManageWindow {

    private final Player player;
    private final WindowUI window;
    private final Main plugin = Main.getInstance();

    public HomeManageWindow(Player player, PlayerProfile profile, String homeName) {
        this.player = player;
        Map<String, Home> homes = profile.getHomes();
        this.window = new WindowUI("&7Zarządzaj Home", 1);

        ItemStack deleteHome = new StackBuilder(new ItemStack(Material.RED_CONCRETE))
                .name("&cUsuń home " + homeName)
                .lores(Collections.singletonList("&7Lewy klik = usuń home"))
                .build();

        window.setSlot(0, deleteHome, event -> {
            homes.remove(homeName);
            plugin.getRepository().save(profile);
            player.sendMessage(ChatUtil.fixColors("&#FF5555Usunięto home " + homeName));
            new HomeWindow(player, profile).open();
        });
    }

    public void open() {
        window.openFor(player);
    }
}
