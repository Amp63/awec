package amp.awec.permission;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.util.helper.UUIDHelper;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Environment(EnvType.SERVER)
public class WorldEditWhitelist {
	private static final String WHITELIST_PATH = "worldedit-whitelist.txt";
	private static File whitelistFile;
	private static final Set<UUID> whitelist = new HashSet<>();
	public static void initialize() {
		whitelistFile = new File(WHITELIST_PATH);

		try {
			whitelistFile.createNewFile();
		} catch (IOException e) {
			System.err.println("Error creating file: " + e.getMessage());
		}

		loadFromFile();
	}

	private static synchronized boolean loadFromFile() {
		whitelist.clear();

		FileReader fileReader;
		try {
			fileReader = new FileReader(whitelistFile);
		}
		catch (FileNotFoundException e) {
			return false;
		}

		try {
			BufferedReader reader = new BufferedReader(fileReader);
			String entry;
			while ((entry = reader.readLine()) != null) {
				if (UUIDHelper.isUUID(entry)) {
					whitelist.add(UUID.fromString(entry));
				}
			}

			reader.close();
		}
		catch (IOException e) {
			return false;
		}

		return true;
	}

	private static synchronized boolean writeToFile() {
		try {
			PrintWriter printwriter = new PrintWriter(new FileWriter(whitelistFile, false));
			for(UUID uuid : whitelist) {
				printwriter.println(uuid.toString());
			}
			printwriter.close();
		}
		catch (IOException e) {
			return false;
		}

		return true;
	}

	public static boolean addUUID(UUID uuid) {
		boolean added = whitelist.add(uuid);
		if (added) {
			writeToFile();
		}
		return added;
	}

	public static boolean removeUUID(UUID uuid) {
		boolean removed = whitelist.remove(uuid);
		if (removed) {
			writeToFile();
		}
		return removed;
	}

	public static boolean isWhitelisted(UUID uuid) {
		return whitelist.contains(uuid);
	}
}
