package pl.endixon.sectors.common.packet;

import pl.endixon.sectors.common.Common;

public final class PacketFlowLogger {

    private boolean enabled = false;

    public void enable() {
        this.enabled = true;
        Common.getInstance().getLogger().info("PacketFlowLogger enabled (FULL TRAFFIC - NO FILTERS).");
    }

    public <T extends Packet> void logIncoming(String subject, T packet) {
        if (!this.enabled) return;
        Common.getInstance().getLogger().info(String.format("[NATS IN]  %s -> %s", subject, packet.getClass().getSimpleName()));
    }

    public void logOutgoing(String subject, Packet packet) {
        if (!this.enabled) return;

        Common.getInstance().getLogger().info(String.format("[NATS OUT] %s -> %s", subject, packet.getClass().getSimpleName()));
    }
}