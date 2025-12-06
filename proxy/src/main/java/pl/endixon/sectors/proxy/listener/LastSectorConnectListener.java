public class LastSectorConnectListener {

    private final VelocitySectorPlugin plugin;
    private final MongoManager mongo;
    private final TeleportationManager teleportManager;

    @Inject
    public LastSectorConnectListener(VelocitySectorPlugin plugin, TeleportationManager teleportManager) {
        this.plugin = plugin;
        this.mongo = plugin.getMongoManager();
        this.teleportManager = teleportManager;
    }

    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        String connectedServer = event.getServer().getServerInfo().getName();

        if (!connectedServer.equalsIgnoreCase("queue")) return;

        QueueManager queueService = plugin.getQueueManager();
        queueService.findQueueByPlayer(player).ifPresent(queue -> queue.removePlayer(player));
        pollForUser(player, queueService);
    }

    private void pollForUser(Player player, QueueManager queueService) {
        plugin.getProxy().getScheduler().buildTask(plugin, () -> {
            CompletableFuture.supplyAsync(() ->
                    mongo.getUsersCollection()
                            .find(new Document("Name", player.getUsername()))
                            .first(),
                    MongoExecutor.EXECUTOR
            ).thenAccept(doc -> {
                if (doc == null) return; 
                String lastSector = doc.getString("sectorName");
                if (lastSector == null) return;

                Queue queue = queueService.getMap().computeIfAbsent(lastSector, Queue::new);
                queue.addPlayer(player);
            });
        }).delay(1500, TimeUnit.MILLISECONDS).schedule();
    }
}
