/*
 *
 * EndSectors – Non-Commercial License
 * (c) 2025 Endixon
 *
 * Permission is granted to use, copy, and
 * modify this software **only** for personal
 * or educational purposes.
 *
 * Commercial use, redistribution, claiming
 * this work as your own, or copying code
 * without explicit permission is strictly
 * prohibited.
 *
 * Visit https://github.com/Endixon/EndSectors
 * for more info.
 *
 */

package pl.endixon.sectors.tools;

import com.mongodb.client.MongoCollection;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import pl.endixon.sectors.common.Common;
import pl.endixon.sectors.common.packet.PacketChannel;
import pl.endixon.sectors.paper.SectorsAPI;
import pl.endixon.sectors.paper.hook.CommonHeartbeatHook;
import pl.endixon.sectors.tools.command.*;
import pl.endixon.sectors.tools.config.ConfigLoader;
import pl.endixon.sectors.tools.config.MessageLoader;
import pl.endixon.sectors.tools.hook.VaultEconomyHook;
import pl.endixon.sectors.tools.manager.CombatManager;
import pl.endixon.sectors.tools.manager.MongoManager;
import pl.endixon.sectors.tools.nats.listener.PacketMarketNotifyListener;
import pl.endixon.sectors.tools.nats.listener.PacketMarketUpdateListener;
import pl.endixon.sectors.tools.nats.packet.PacketMarketNotify;
import pl.endixon.sectors.tools.nats.packet.PacketMarketUpdate;
import pl.endixon.sectors.tools.user.Repository.MarketRepository;
import pl.endixon.sectors.tools.user.listeners.CombatListener;
import pl.endixon.sectors.tools.user.listeners.InventoryInternactListener;
import pl.endixon.sectors.tools.user.listeners.ProfileListener;
import pl.endixon.sectors.tools.user.profile.PlayerProfile;
import pl.endixon.sectors.tools.user.Repository.PlayerRepository;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import pl.endixon.sectors.tools.utils.LoggerUtil;

import java.io.File;

@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    private MongoManager mongoService;
    private SectorsAPI sectorsAPI;
    private CommonHeartbeatHook heartbeatHook;
    private ConfigLoader configLoader;
    private MessageLoader messageLoader;
    private PlayerRepository repository;
    private MarketRepository marketRepository;
    private CombatManager combatManager;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        Common.initInstance();
        this.loadConfigs();

        if (!this.initSectorsAPI()) {
            this.shutdown("EndSectors API dependency not found or disabled! Shutting down.");
            return;
        }


        Common.getInstance().initializeRedis(
                configLoader.redisHost,
                configLoader.redisPort,
                configLoader.redisPassword
        );

        Common.getInstance().initializeNats(
                configLoader.natsUrl,
                configLoader.natsConnectionName
        );


        this.heartbeatHook = new CommonHeartbeatHook(this);
        this.heartbeatHook.checkConnection();
        this.initNatsSubscriptions();

        this.initMongo();
        this.initRepositories();
        this.combatManager = new CombatManager(this);
        this.registerVault();
        this.registerCommands();
        this.registerListeners();
        LoggerUtil.info("EndSectors-Tools successfully enabled and synchronized.");
    }

    @Override
    public void onDisable() {
        this.shutdownMongo();
        LoggerUtil.info("EndSectors-Tools disabled. Resources released.");
    }


    private void loadConfigs() {
        LoggerUtil.info("Loading JSON configuration files...");
        File dataFolder = this.getDataFolder();
        this.messageLoader = MessageLoader.load(dataFolder);
        this.configLoader = ConfigLoader.load(dataFolder);
        LoggerUtil.info("Configuration and messages loaded successfully.");
    }

    private void initNatsSubscriptions() {
        var nats = Common.getInstance().getNatsManager();

        nats.subscribe(
                PacketChannel.MARKET_UPDATE.getSubject(),
                new PacketMarketUpdateListener(),
                PacketMarketUpdate.class
        );

        nats.subscribe(
                PacketChannel.MARKET_NOTIFY.getSubject(),
                new PacketMarketNotifyListener(),
                PacketMarketNotify.class
        );
    }

    private void initMongo() {
        LoggerUtil.info("Connecting to MongoDB cluster...");
        this.mongoService = new MongoManager();
        this.mongoService.connect(configLoader.mongoUri, configLoader.mongoDatabase);
    }

    private void initRepositories() {
        LoggerUtil.info("Initializing MongoDB repositories...");
        try {

            MongoCollection<PlayerProfile> playerCollection = this.mongoService.getDatabase().getCollection("players", PlayerProfile.class);
            this.repository = new PlayerRepository(playerCollection);
            MongoCollection<PlayerMarketProfile> marketCollection = this.mongoService.getDatabase().getCollection("market", PlayerMarketProfile.class);
            this.marketRepository = new MarketRepository(marketCollection);
            this.marketRepository.warmup();
            LoggerUtil.info("Repositories initialized successfully (Cached in RAM).");

        } catch (Exception e) {
            LoggerUtil.error("Critical error: Failed to initialize MongoDB repositories!");
            LoggerUtil.error("Context: " + e.getMessage());
            this.getLogger().log(java.util.logging.Level.SEVERE, "Detailed repository trace:", e);
            this.shutdown("Database repository failure – check MongoDB connection.");
        }
    }


    private boolean initSectorsAPI() {
        var plugin = Bukkit.getPluginManager().getPlugin("EndSectors");
        if (plugin == null || !plugin.isEnabled()) {
            LoggerUtil.info("EndSectors API is missing or disabled!");
            return false;
        }
        try {
            this.sectorsAPI = SectorsAPI.getInstance();
            return this.sectorsAPI != null;
        } catch (Exception e) {
            LoggerUtil.info("Critical error during SectorsAPI initialization: " + e.getMessage());
            return false;
        }
    }

    private void registerVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            LoggerUtil.info("Vault not found! Economy features will be limited.");
            return;
        }
        this.economy = new VaultEconomyHook();

        getServer().getServicesManager().register(
                Economy.class,
                this.economy,
                this,
                ServicePriority.Highest
        );
        LoggerUtil.info("VaultEconomyHook has been registered.");
    }


    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ProfileListener(this.repository), this);
        pm.registerEvents(new CombatListener(this.combatManager, this.sectorsAPI), this);
        pm.registerEvents(new InventoryInternactListener(), this);
        LoggerUtil.info("System event listeners registered.");
    }

    private void registerCommands() {
        this.setupCommand("randomtp", new RandomTPCommand(this.sectorsAPI));
        this.setupCommand("spawn", new SpawnCommand(this.sectorsAPI));
        this.setupCommand("home", new HomeCommand(this.sectorsAPI));
        MarketCommand marketCommand = new MarketCommand();
        this.setupCommand("market", marketCommand);
        this.setupCommand("ah", marketCommand);

        EconomyCommand balanceCommand = new EconomyCommand();
        this.setupCommand("balance", balanceCommand);
        this.setupCommand("bal", balanceCommand);
        this.setupCommand("money", balanceCommand);
        this.setupCommand("eco", balanceCommand);

        LoggerUtil.info("Command executors synchronized.");
    }


    private void setupCommand(String name, Object executor) {
        PluginCommand command = this.getCommand(name);
        if (command == null) {
            LoggerUtil.info("Command /" + name + " is missing from plugin.yml!");
            return;
        }
        command.setExecutor((CommandExecutor) executor);
        if (executor instanceof TabCompleter) {
            command.setTabCompleter((TabCompleter) executor);
        }
    }

    private void shutdownMongo() {
        if (this.mongoService != null) {
            this.mongoService.disconnect();
            LoggerUtil.info("MongoDB connection closed.");
        }
    }

    private void shutdown(String reason) {
        LoggerUtil.info("Shutting down due to: " + reason);
        Bukkit.getPluginManager().disablePlugin(this);
    }
}