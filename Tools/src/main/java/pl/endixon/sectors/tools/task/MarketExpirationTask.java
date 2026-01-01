package pl.endixon.sectors.tools.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import pl.endixon.sectors.tools.market.MarketService;


@RequiredArgsConstructor
public class MarketExpirationTask extends BukkitRunnable {

    private final MarketService marketService;

    @Override
    public void run() {
        if (marketService == null) return;
        marketService.runExpirationTask();
    }
}