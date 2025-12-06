package pl.endixon.sectors.proxy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigCreator {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void createDefaultConfig(Path dataFolder) {
        try {
            if (!Files.exists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }

            Path configPath = dataFolder.resolve("config.json");
            if (Files.exists(configPath)) {
                System.out.println("config.json już istnieje, pomijam tworzenie.");
                return;
            }

            Map<String, Object> root = new LinkedHashMap<>();
            Map<String, Object> sectors = new LinkedHashMap<>();
            Map<String, Object> spawn = new LinkedHashMap<>();
            spawn.put("spawn01", createSectorMap(-250, -250, 250, 250, "SPAWN", "world"));
            spawn.put("spawn02", createSectorMap(-250, -250, 250, 250, "SPAWN", "world"));
            sectors.put("SPAWN", spawn);
            Map<String, Object> queue = new LinkedHashMap<>();
            queue.put("queue", createSectorMap(-50, -50, 50, 50, "QUEUE", "world"));
            sectors.put("QUEUE", queue);
            Map<String, Object> sector = new LinkedHashMap<>();
            sector.put("north", createSectorMap(250, -250, 750, 250, "SECTOR", "world"));
            sector.put("south", createSectorMap(-750, -250, -250, 250, "SECTOR", "world"));
            sector.put("east", createSectorMap(-250, 250, 250, 750, "SECTOR", "world"));
            sector.put("west", createSectorMap(-250, -750, 250, -250, "SECTOR", "world"));
            sector.put("northEast", createSectorMap(250, 250, 750, 750, "SECTOR", "world"));
            sector.put("northWest", createSectorMap(-750, 250, -250, 750, "SECTOR", "world"));
            sector.put("southEast", createSectorMap(250, -750, 750, -250, "SECTOR", "world"));
            sector.put("southWest", createSectorMap(-750, -750, -250, -250, "SECTOR", "world"));
            sectors.put("SECTOR", sector);
            Map<String, Object> nether = new LinkedHashMap<>();
            nether.put("nether01", createSectorMap(-128, -128, 128, 128, "NETHER", "world_nether"));
            nether.put("nether02", createSectorMap(129, -128, 384, 128, "NETHER", "world_nether"));
            sectors.put("NETHER", nether);
            Map<String, Object> end = new LinkedHashMap<>();
            end.put("end01", createSectorMap(-100, -100, 100, 100, "END", "world_end"));
            end.put("end02", createSectorMap(101, -100, 300, 100, "END", "world_end"));
            sectors.put("END", end);
            root.put("sectors", sectors);
            mapper.writerWithDefaultPrettyPrinter().writeValue(configPath.toFile(), root);
            System.out.println("config.json został utworzony!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> createSectorMap(int pos1X, int pos1Z, int pos2X, int pos2Z, String type, String world) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pos1X", pos1X);
        map.put("pos1Z", pos1Z);
        map.put("pos2X", pos2X);
        map.put("pos2Z", pos2Z);
        map.put("type", type);
        map.put("world", world);
        return map;
    }
}
