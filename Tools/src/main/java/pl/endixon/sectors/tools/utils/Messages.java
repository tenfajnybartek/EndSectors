package pl.endixon.sectors.tools.utils;

import pl.endixon.sectors.common.util.ChatUtil;

public enum Messages {


    CONSOLE_BLOCK(ChatUtil.fixHexColors("&#ef4444Ta komenda jest tylko dla gracza")),
    SPAWN_TITLE(ChatUtil.fixHexColors("&#ff5555Błąd")),
    PORTAL_COMBAT_TITLE(ChatUtil.fixHexColors("&#ff5555Błąd")),
    PORTAL_COMBAT_SUBTITLE(ChatUtil.fixHexColors("&#ef4444Nie możesz użyć portalu podczas walki")),
    SECTOR_COMBAT_TITLE(ChatUtil.fixHexColors("&#ff5555Błąd")),
    SECTOR_COMBAT_SUBTITLE(ChatUtil.fixHexColors("&#ef4444Nie możesz opuścić sektora podczas walki")),

    SPAWN_OFFLINE(ChatUtil.fixHexColors("&#ef4444Ten sektor jest aktualnie wyłączony")),
    SPAWN_ALREADY(ChatUtil.fixHexColors("&#ef4444Już jesteś juz na spawnie")),
    RANDOM_TITLE(ChatUtil.fixHexColors("&#4ade80RandomTP")),
    RANDOM_START(ChatUtil.fixHexColors("&#9ca3afLosowanie sektora... &#4ade80proszę czekać"));

    private final String text;

    Messages(String text) {
        this.text = text;
    }

    public String get() {
        return ChatUtil.fixColors(text);
    }
}
