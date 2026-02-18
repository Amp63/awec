package amp.awec.data;

import amp.awec.BlockPos;
import amp.awec.util.BlockState;

import java.util.ArrayList;

public class PlayerData {
	public BlockPos corner1 = null;
	public BlockPos corner2 = null;

	public ArrayList<BlockState> clipboardBlocks = new ArrayList<>();

	public boolean hasBothCorners() {
		return corner1 != null && corner2 != null;
	}
}
