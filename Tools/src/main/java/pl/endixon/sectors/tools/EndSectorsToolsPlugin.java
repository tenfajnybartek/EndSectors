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
import pl.endixon.sectors.tools.market.MarketService;
import pl.endixon.sectors.tools.market.listener.ProfileMarketJoinListener;
import pl.endixon.sectors.tools.market.repository.MarketRepository;
import pl.endixon.sectors.tools.nats.listener.PacketMarketExpirationNotifyListener;
import pl.endixon.sectors.tools.nats.listener.PacketMarketJanitorListener;
import pl.endixon.sectors.tools.nats.listener.PacketMarketNotifyListener;
import pl.endixon.sectors.tools.nats.listener.PacketMarketUpdateListener;
import pl.endixon.sectors.tools.nats.packet.PacketMarketExpirationNotify;
import pl.endixon.sectors.tools.nats.packet.PacketMarketJanitor;
import pl.endixon.sectors.tools.nats.packet.PacketMarketNotify;
import pl.endixon.sectors.tools.nats.packet.PacketMarketUpdate;
import pl.endixon.sectors.tools.task.MarketBossBarTask;
import pl.endixon.sectors.tools.task.MarketExpirationTask;
import pl.endixon.sectors.tools.user.Repository.PlayerRepository;
import pl.endixon.sectors.tools.user.listeners.CombatListener;
import pl.endixon.sectors.tools.user.listeners.InventoryInternactListener;
import pl.endixon.sectors.tools.user.listeners.ProfileListener;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import pl.endixon.sectors.tools.user.profile.PlayerProfile;
import pl.endixon.sectors.tools.utils.LoggerUtil;

import java.io.File;
import java.util.logging.Level;

@Getter
public class EndSectorsToolsPlugin extends JavaPlugin {

    @Getter
    private static EndSectorsToolsPlugin instance;
    private MongoManager mongoService;
    private SectorsAPI sectorsAPI;
    private CommonHeartbeatHook heartbeatHook;
    private ConfigLoader configLoader;
    private MessageLoader messageLoader;
    private PlayerRepository repository;
    private MarketRepository marketRepository;
    private MarketService marketService;
    private CombatManager combatManager;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        long start = System.currentTimeMillis();
        Common.initInstance();
        this.loadConfigs();

        if (!this.initSectorsAPI()) {
            this.shutdown("EndSectors API dependency missing! Aborting start.");
            return;
        }

        this.initNetworkServices();
        this.initMongo();
        this.initDataLayer();
        this.initScheduledTasks();
        this.heartbeatHook = new CommonHeartbeatHook(this);
        this.heartbeatHook.checkConnection();
        this.combatManager = new CombatManager(this);
        this.registerVault();
        this.registerCommands();
        this.registerListeners();
        this.initNatsSubscriptions();

        LoggerUtil.info("EndSectors-Tools initialized in " + (System.currentTimeMillis() - start) + "ms.");
    }

    @Override
    public void onDisable() {
        this.shutdownMongo();
        LoggerUtil.info("EndSectors-Tools disabled. Good night.");
    }

    private void loadConfigs() {
        LoggerUtil.info("Loading configuration context...");
        File dataFolder = this.getDataFolder();
        this.messageLoader = MessageLoader.load(dataFolder);
        this.configLoader = ConfigLoader.load(dataFolder);
    }

    private void initNetworkServices() {
        Common.getInstance().initializeRedis(
                configLoader.redisHost,
                configLoader.redisPort,
                configLoader.redisPassword
        );
        Common.getInstance().initializeNats(
                configLoader.natsUrl,
                configLoader.natsConnectionName
        );
    }

    private void initNatsSubscriptions() {
        var nats = Common.getInstance().getNatsManager();
        nats.subscribe(PacketChannel.MARKET_UPDATE.getSubject(), new PacketMarketUpdateListener(), PacketMarketUpdate.class);
        nats.subscribe(PacketChannel.MARKET_NOTIFY.getSubject(), new PacketMarketNotifyListener(), PacketMarketNotify.class);
        nats.subscribe(PacketChannel.MARKET_JANITOR.getSubject(), new PacketMarketJanitorListener(), PacketMarketJanitor.class);
        nats.subscribe(PacketChannel.MARKET_EXPIRATION_NOTIFY.getSubject(), new PacketMarketExpirationNotifyListener(), PacketMarketExpirationNotify.class);
    }

    private void initMongo() {
        LoggerUtil.info("Mounting MongoDB connection...");
        this.mongoService = new MongoManager();
        this.mongoService.connect(configLoader.mongoUri, configLoader.mongoDatabase);
    }


    private void initScheduledTasks() {
        LoggerUtil.info("Scheduling background maintenance tasks...");
     //   new MarketExpirationTask(this.marketService).runTaskTimerAsynchronously(this, 1200L, 12000L);
        new MarketExpirationTask(this.marketService).runTaskTimerAsynchronously(this, 100L, 200L);
        new MarketBossBarTask(this).runTaskTimer(this, 100L, 40L);
        LoggerUtil.info("Market Expiration Task registered (Async).");
    }

    private void initDataLayer() {
        LoggerUtil.info("Bootstrapping Data & Service Layer...");
        try {
            MongoCollection<PlayerProfile> playerCollection = this.mongoService.getDatabase().getCollection("players", PlayerProfile.class);
            this.repository = new PlayerRepository(playerCollection);
            MongoCollection<PlayerMarketProfile> marketCollection = this.mongoService.getDatabase().getCollection("market", PlayerMarketProfile.class);
            this.marketRepository = new MarketRepository(marketCollection);
            this.marketRepository.warmup();
            this.marketService = new MarketService(this.marketRepository);
            LoggerUtil.info("Services active. Market cache warmed up.");
        } catch (Exception e) {
            LoggerUtil.error("Critical dependency injection failure!");
            this.getLogger().log(Level.SEVERE, "Stacktrace:", e);
            this.shutdown("Persistence layer failure.");
        }
    }

    private boolean initSectorsAPI() {
        var plugin = Bukkit.getPluginManager().getPlugin("EndSectors");
        if (plugin == null || !plugin.isEnabled()) return false;
        try {
            this.sectorsAPI = SectorsAPI.getInstance();
            return this.sectorsAPI != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void registerVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            LoggerUtil.warn("Vault missing. Economy hook skipped.");
            return;
        }
        this.economy = new VaultEconomyHook();
        getServer().getServicesManager().register(Economy.class, this.economy, this, ServicePriority.Highest);
        LoggerUtil.info("Vault hooked.");
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ProfileListener(this.repository), this);
        pm.registerEvents(new CombatListener(this.combatManager, this.sectorsAPI), this);
        pm.registerEvents(new InventoryInternactListener(), this);
        pm.registerEvents(new ProfileMarketJoinListener(this), this);

    }

    private void registerCommands() {
        this.setupCommand("randomtp", new RandomTPCommand(this.sectorsAPI));
        this.setupCommand("spawn", new SpawnCommand(this.sectorsAPI));
        this.setupCommand("home", new HomeCommand(this.sectorsAPI));
        MarketCommand marketCommand = new MarketCommand();
        this.setupCommand("market", marketCommand);
        MarketSellCommand marketSellCommand = new MarketSellCommand();
        this.setupCommand("wystaw", marketSellCommand);
        EconomyCommand balanceCommand = new EconomyCommand();
        this.setupCommand("balance", balanceCommand);
        LoggerUtil.info("Command Executors registered.");
    }

    private void setupCommand(String name, Object executor) {
        PluginCommand command = this.getCommand(name);
        if (command == null) {
            LoggerUtil.warn("Command defined in code but missing in plugin.yml: /" + name);
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
        }
    }

    private void shutdown(String reason) {
        LoggerUtil.error("SHUTDOWN SEQUENCE INITIATED: " + reason);
        Bukkit.getPluginManager().disablePlugin(this);
    }
}