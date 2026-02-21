package amp.awec.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
	public static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

	public static PlayerData getPlayerData(UUID playerUuid) {
		if (!PLAYER_DATA.containsKey(playerUuid)) {
			// Create new entry if it doesn't exist yet
			PlayerData data = new PlayerData();
			PLAYER_DATA.put(playerUuid, data);
			return data;
		}

		return PLAYER_DATA.get(playerUuid);
	}

	public static boolean deletePlayerData(UUID playerUuid) {
		return PLAYER_DATA.remove(playerUuid) != null;
	}
}
