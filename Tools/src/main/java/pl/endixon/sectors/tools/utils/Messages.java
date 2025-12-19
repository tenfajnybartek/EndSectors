package pl.endixon.sectors.tools.utils;

import pl.endixon.sectors.common.util.ChatUtil;

public enum Messages {

    CONSOLE_BLOCK("&cTa komenda jest tylko dla gracza"),
    SPAWN_TITLE(ChatUtil.fixHexColors("&#FFD700Spawn")),
    SPAWN_OFFLINE(ChatUtil.fixHexColors("&#FF5555Spawn aktualnie offline")),
    SPAWN_ALREADY(ChatUtil.fixHexColors("&#FF5555Już jesteś na spawnie")),
    RANDOM_TITLE(ChatUtil.fixHexColors("&#00FFFFRandomTP")),
    RANDOM_START(ChatUtil.fixHexColors("&#AAAAAALosowanie sektora..."));


    private final String text;

    Messages(String text) {
        this.text = text;
    }

    public String get() {
        return ChatUtil.fixColors(text);
    }

    public String format(String key, String value) {
        return ChatUtil.fixColors(text.replace("%" + key + "%", value));
    }

    public String format(int time) {
        return ChatUtil.fixColors(text.replace("%time%", String.valueOf(time)));
    }



    public String format(String key1, String value1, String key2, String value2) {
        return ChatUtil.fixColors(
                text.replace("%" + key1 + "%", value1)
                        .replace("%" + key2 + "%", value2)
        );
    }
}
