package amp.awec.data;

import amp.awec.WorldEditMod;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolume;
import amp.awec.volume.CuboidVolumeBuffer;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

class PlayerDataWorld {
	public CuboidVolume selection = new CuboidVolume(null, null);
	public UndoHistory undoHistory = new UndoHistory();
}

public class PlayerData {
	public boolean wandEnabled = true;

	public CuboidVolumeBuffer clipboardBuffer = null;
	public Vec3i copyOffset = null;

	private final Map<Dimension, PlayerDataWorld> worldSpecificData = new HashMap<>();

	private void checkCreateWorldData(World world) {
		if (!worldSpecificData.containsKey(world.dimension)) {
			PlayerDataWorld data = new PlayerDataWorld();
			worldSpecificData.put(world.dimension, data);
			WorldEditMod.LOGGER.info("Created new WorldEdit data in dimension \"" + world.dimension.getTranslatedName() + "\"");
		}
	}

	public CuboidVolume getSelection(@NotNull World world) {
		checkCreateWorldData(world);
		return worldSpecificData.get(world.dimension).selection;
	}

	public UndoHistory getUndoHistory(@NotNull World world) {
		checkCreateWorldData(world);
		return worldSpecificData.get(world.dimension).undoHistory;
	}
}
