package amp.awec.data;

import amp.awec.util.Vec3i;
import amp.awec.util.CuboidVolume;
import amp.awec.volume.CopiedVolume;

public class PlayerData {
	public CuboidVolume selection = new CuboidVolume(null, null);
	public boolean wandEnabled = true;

	public CopiedVolume clipboardVolume = null;
	public Vec3i copyOffset = null;
}
