package amp.awec.data;

import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolume;
import amp.awec.volume.CopiedVolume;
import net.minecraft.core.entity.player.Player;

public class PlayerData {
	public Player parentPlayer;
	public CuboidVolume selection = new CuboidVolume(null, null);
	public boolean wandEnabled = true;

	public CopiedVolume clipboardVolume = null;
	public Vec3i copyOffset = null;

	public UndoHistory undoHistory = new UndoHistory();

	public PlayerData(Player parent) {
		parentPlayer = parent;
	}
}
