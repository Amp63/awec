package amp.awec.command;

import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import amp.awec.operation.WorldChange;
import amp.awec.volume.CuboidVolume;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandPlayerData {
	public @NotNull World world;
	public @NotNull Player player;
	public @NotNull PlayerData data;

	public CommandPlayerData(@NotNull World world, @NotNull Player player, @NotNull PlayerData data) {
		this.world = world;
		this.player = player;
		this.data = data;
	}

	public static @Nullable CommandPlayerData get(CommandSource source) {
		Player player = source.getSender();
		if (player == null) {
			return null;
		}

		PlayerData playerData = PlayerDataManager.getPlayerData(player.uuid);

		World world = source.getWorld();
		if (world == null) {
			return null;
		}

		if (!playerData.getSelection(world).isComplete()) {
			source.sendMessage("Both corners must be set");
			return null;
		}

		return new CommandPlayerData(world, player, playerData);
	}

	public CuboidVolume getSelection() {
		return data.getSelection(world);
	}

	public void addUndoChange(WorldChange change) {
		data.getUndoHistory(world).add(change);
	}
}
