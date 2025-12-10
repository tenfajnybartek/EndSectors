package pl.endixon.sectors.tools;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.sector.SectorManager;
import pl.endixon.sectors.tools.command.RandomTPCommand;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    private SectorManager sectorManager;

    @Override
    public void onEnable() {
        instance = this;

        PaperSector paperSector = (PaperSector) Bukkit.getPluginManager().getPlugin("EndSectors");
        if (paperSector == null) {
            getLogger().severe("EndSectors nie znaleziony, wyłączam plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.sectorManager = paperSector.getSectorManager();

        getCommand("randomtp").setExecutor(new RandomTPCommand(sectorManager));

        getLogger().info("Uruchomiono EndSectors-Tools");
    }

}
