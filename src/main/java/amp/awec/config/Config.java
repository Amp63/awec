package amp.awec.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.item.Item;
import org.jetbrains.annotations.Nullable;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {
	public static final String CONFIG_FILE_NAME = "worldedit.properties";
	public static final Path CONFIG_PATH = FabricLoader.getInstance().getGameDir().resolve("config").resolve(CONFIG_FILE_NAME);

	public static Properties config;

	public static String WAND_ITEM = "item.tool.axe.wood";
	public static int MAX_UNDO_HISTORY = 100;
	public static double THRU_MAX_RAY_DISTANCE = 50.0;
	public static double THRU_MARCH_DISTANCE = 1.0;
	public static double THRU_MAX_THRU_DISTANCE = 50.0;
	public static String SCHEMATIC_SAVE_DIRECTORY = "schematics";

	public static void load() throws IOException {
		if (!Files.exists(CONFIG_PATH)) {
			loadDefaults();
		}

		config = new Properties();
		config.load(new FileReader(CONFIG_PATH.toString()));

		WAND_ITEM = getString("tool.wand_item");
		MAX_UNDO_HISTORY = getInt("undo.max_undo_history");
		THRU_MAX_RAY_DISTANCE = getDouble("thru.max_ray_distance");
		THRU_MARCH_DISTANCE = getDouble("thru.march_distance");
		THRU_MAX_THRU_DISTANCE = getDouble("thru.max_thru_distance");
		SCHEMATIC_SAVE_DIRECTORY = getString("schem.save_directory");
	}

	public static @Nullable String getString(String key) {
		return config.getProperty(key);
	}

	public static int getInt(String key) {
		return Integer.parseInt(config.getProperty(key));
	}

	public static double getDouble(String key) {
		return Double.parseDouble(config.getProperty(key));
	}

	private static void loadDefaults() throws IOException {
		config = new Properties();
		config.setProperty("tool.wand_item", WAND_ITEM);
		config.setProperty("undo.max_undo_history", String.valueOf(MAX_UNDO_HISTORY));
		config.setProperty("thru.max_ray_distance", String.valueOf(THRU_MAX_RAY_DISTANCE));
		config.setProperty("thru.march_distance", String.valueOf(THRU_MARCH_DISTANCE));
		config.setProperty("thru.max_thru_distance", String.valueOf(THRU_MAX_THRU_DISTANCE));
		config.setProperty("schem.save_directory", SCHEMATIC_SAVE_DIRECTORY);
		config.store(new FileWriter(CONFIG_PATH.toString()), "WorldEdit config");
	}
}
