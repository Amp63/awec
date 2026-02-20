package amp.awec.command;

import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import org.jetbrains.annotations.Nullable;

public class CommandHelper {
	public static @Nullable PlayerData getPlayerData(CommandSource source) {
		Player player = source.getSender();
		if (player == null) {
			return null;
		}

		PlayerData playerData = WorldEditMod.getPlayerData(player);
		if (playerData == null) {
			source.sendMessage("Failed to access WorldEdit player data");
			return null;
		}
		if (!playerData.selection.isComplete()) {
			source.sendMessage("Both corners must be set");
			return null;
		}

		return playerData;
	}
}
