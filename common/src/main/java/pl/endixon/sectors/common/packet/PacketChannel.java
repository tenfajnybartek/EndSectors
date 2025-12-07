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

package pl.endixon.sectors.common.packet;

public interface PacketChannel {

    String PROXY = "proxy";
    String SECTORS = "sectors";
    String GLOBAL = "global";
    String SPAWN = "spawn";
    String QUEUE = "queue";
    String END = "end";
    String PROXY_TO_PAPER = "PROXY_TO_PAPER";
    String PAPER_TO_PROXY = "PAPER_TO_PROXY";
}

