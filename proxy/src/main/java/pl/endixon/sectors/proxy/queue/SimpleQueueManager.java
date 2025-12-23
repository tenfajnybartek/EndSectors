package pl.endixon.sectors.proxy.queue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleQueueManager<T, ID> {

    private final Map<T, ID> map = new ConcurrentHashMap<>();

    public Map<T, ID> getMap() {
        return this.map;
    }
}