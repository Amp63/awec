package amp.awec.operation;

import amp.awec.pattern.BlockMask;
import amp.awec.pattern.BlockPattern;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolumeBuffer;
import amp.awec.volume.CuboidVolume;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;

public class MoveOperation {
	public static WorldChange execute(World world, CuboidVolume volume, int amount, Direction direction) {
		Vec3i directionVec = new Vec3i(direction);
		Vec3i shiftVector = directionVec.scale(amount);
		Vec3i setPos = new Vec3i(volume.getMinCorner());
		setPos.addi(shiftVector);

		CuboidVolumeBuffer cuboidVolumeBuffer = CuboidVolumeBuffer.copyFrom(world, volume);
		WorldChange setResult = SetOperation.execute(world, volume, new BlockPattern((Block<?>) null));
		WorldChange moveResult = cuboidVolumeBuffer.setAt(world, setPos, BlockMask.ANY);
		moveResult.update(setResult);

		return moveResult;
	}
}
