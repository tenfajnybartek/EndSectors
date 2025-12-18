package pl.endixon.sectors.paper.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import pl.endixon.sectors.paper.PaperSector;

public class DeathScreenPacketutils {

    public static void register(PaperSector plugin) {

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.NORMAL,
                        PacketType.Play.Server.GAME_STATE_CHANGE) {

                    @Override
                    public void onPacketSending(PacketEvent event) {
                        int reason = event.getPacket().getIntegers().read(0);
                        if (reason == 3) {
                            event.setCancelled(true);
                        }
                    }
                }
        );
    }
}
