package pl.endixon.sectors.tools.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ChatAdventureUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    public String toLegacyString(String message) {
        if (message == null || message.isEmpty()) return "";
        return LEGACY_SERIALIZER.serialize(this.toComponent(message));
    }


    public Component toComponent(String message) {
        if (message == null || message.isEmpty()) return Component.empty();
        Component component = parseMessage(message);
        return component.decoration(TextDecoration.ITALIC, false);
    }

    private Component parseMessage(String message) {

        if (message.contains("§")) {
            return LEGACY_SERIALIZER.deserialize(message);
        }
        String modernized = message.replace("&#", "<#").replace("}", ">");
        if (modernized.contains("&") && !modernized.contains("<")) {
            return AMPERSAND_SERIALIZER.deserialize(modernized);
        }
        try {
            return MINI_MESSAGE.deserialize(modernized);
        } catch (Exception e) {
            return AMPERSAND_SERIALIZER.deserialize(modernized);
        }
    }
}