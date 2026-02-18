package amp.awec;

import amp.awec.util.BlockState;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.Items;

import java.util.ArrayList;

public class ModState {
	public static BlockPos corner1 = null;
	public static BlockPos corner2 = null;

	public static ArrayList<BlockState> clipboardBlocks = new ArrayList<>();

	public static boolean CheckCorners() {
		return corner1 != null && corner2 != null;
	}
}
