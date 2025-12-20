package pl.endixon.sectors.paper.util;

import pl.endixon.sectors.common.util.ChatUtil;

public interface Configuration {


    String SECTOR_FULL_TITLE = ChatUtil.fixHexColors("&#ff5555Błąd");
    String SECTOR_FULL_SUBTITLE = ChatUtil.fixHexColors("&#ef4444Sektor jest pełen graczy!");


    String BORDER_MESSAGE = ChatUtil.fixHexColors(
            "&#f5c542Zbliżasz się do granicy sektora &#4ade80{SECTOR} &#7dd3fc{DISTANCE}m"
    );
    int BORDER_MESSAGE_DISTANCE = 15;

    int BREAK_BORDER_DISTANCE = 15;
    String BREAK_BORDER_DISTANCE_MESSAGE = ChatUtil.fixHexColors(
            "&#ef4444Nie możesz niszczyć bloków przy sektorze!"
    );

    int PLACE_BORDER_DISTANCE = 15;
    String PLACE_BORDER_DISTANCE_MESSAGE = ChatUtil.fixHexColors(
            "&#ef4444Nie możesz stawiać bloków przy sektorze!"
    );


    String SECTOR_DISABLED_TITLE = ChatUtil.fixHexColors("&#ff5555Błąd");
    String SECTOR_DISABLED_SUBTITLE = ChatUtil.fixHexColors(
            "&#ef4444Ten sektor jest aktualnie wyłączony"
    );

    int EXPLOSION_BORDER_DISTANCE = 15;
    int BUCKET_BORDER_DISTANCE = 15;

    String playerAlreadyConnectedMessage = ChatUtil.fixHexColors(
            "&#ef4444Jesteś aktualnie połączony z tym kanałem"
    );
    String sectorIsOfflineMessage = ChatUtil.fixHexColors(
            "&#ef4444Sektor z którym chcesz się połączyć jest aktualnie wyłączony!"
    );
    String playerDataNotFoundMessage = ChatUtil.fixHexColors(
            "&#ef4444Profil użytkownika nie został znaleziony!"
    );

    String TITLE_SECTOR_UNAVAILABLE = ChatUtil.fixHexColors("&#ff5555Błąd");
    String TITLE_WAIT_TIME = ChatUtil.fixHexColors(
            "&#ef4444Musisz odczekać {SECONDS}s przed ponowną zmianą sektora"
    );


    String spawnSectorNotFoundMessage = ChatUtil.fixHexColors(
            "&#ef4444Nie odnaleziono dostepnego sektora spawn"
    );
}
