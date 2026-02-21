package amp.awec.util;

import net.minecraft.core.world.World;

public class BlockChange {
	public BlockState blockState;
	public Vec3i position;

	public BlockChange(BlockState block, Vec3i pos) {
		blockState = block;
		position = new Vec3i(pos);
	}

	public BlockChange apply(World world) {
		return blockState.setNotify(world, position);
	}
}
