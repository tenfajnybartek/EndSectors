/*
 *
 *  EndSectors  Non-Commercial License
 *  (c) 2025 Endixon
 *
 *  Permission is granted to use, copy, and
 *  modify this software **only** for personal
 *  or educational purposes.
 *
 *   Commercial use, redistribution, claiming
 *  this work as your own, or copying code
 *  without explicit permission is strictly
 *  prohibited.
 *
 *  Visit https://github.com/Endixon/EndSectors
 *  for more info.
 *
 */

package pl.endixon.sectors.common.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.redisson.api.listener.MessageListener;
import pl.endixon.sectors.common.packet.Packet;

@Getter
@AllArgsConstructor
public abstract class RedisPacketListener<T extends Packet> implements MessageListener<T> {

    private final Class<T> packetClass;

    @Override
    public void onMessage(CharSequence channel, T packet) {
        this.handle(packet);
    }

    public abstract void handle(T packet);
}

