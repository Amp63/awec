package amp.awec.data;

import amp.awec.util.BlockPos;
import amp.awec.util.BlockState;
import amp.awec.volume.CopiedVolume;

import java.util.ArrayList;

public class PlayerData {
	public BlockPos corner1 = null;
	public BlockPos corner2 = null;
	public boolean wandEnabled = true;

	public CopiedVolume clipboardVolume = null;
	public BlockPos copyOffset = null;

	public boolean hasBothCorners() {
		return corner1 != null && corner2 != null;
	}
}
