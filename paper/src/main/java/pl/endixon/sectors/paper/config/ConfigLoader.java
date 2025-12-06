package pl.endixon.sectors.paper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import pl.endixon.sectors.paper.PaperSector;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ConfigLoader {

    public String currentSector = "spawn01"; // default

    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static ConfigLoader load(File dataFolder) {
        try {
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                PaperSector.getInstance().getLogger().warning("Nie udało się utworzyć folderu configu: " + dataFolder.getAbsolutePath());
            }

            File file = new File(dataFolder, "config.json");

            return Optional.of(file)
                    .filter(File::exists)
                    .map(f -> {
                        try {
                            return mapper.readValue(f, ConfigLoader.class);
                        } catch (IOException e) {
                            PaperSector.getInstance().getLogger().warning("Błąd podczas wczytywania config.json, używam default: " + e.getMessage());
                            return new ConfigLoader();
                        }
                    })
                    .orElseGet(() -> {
                        ConfigLoader defaultConfig = new ConfigLoader();
                        try {
                            mapper.writeValue(file, defaultConfig);
                            PaperSector.getInstance().getLogger().info("Utworzono domyślny config.json");
                        } catch (IOException e) {
                            PaperSector.getInstance().getLogger().warning("Nie udało się zapisać domyślnego config.json: " + e.getMessage());
                        }
                        return defaultConfig;
                    });

        } catch (Exception e) {
            PaperSector.getInstance().getLogger().warning("Nieoczekiwany błąd wczytywania configu: " + e.getMessage());
            return new ConfigLoader();
        }
    }
}
