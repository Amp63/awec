package amp.awec.operation;

import amp.awec.pattern.BlockMask;
import amp.awec.pattern.BlockPattern;
import amp.awec.util.DirectionHelper;
import amp.awec.volume.CuboidVolumeBuffer;
import amp.awec.volume.CuboidVolume;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.joml.Vector3i;

public class MoveOperation {
	public static WorldChange execute(World world, CuboidVolume volume, int amount, Direction direction, boolean shiftVolume) {
		Vector3i directionVec = DirectionHelper.getVec3i(direction);
		Vector3i shiftVector = directionVec.mul(amount);
		TilePos setPos = new TilePos(volume.getMinCorner()).add(shiftVector);

		CuboidVolumeBuffer cuboidVolumeBuffer = CuboidVolumeBuffer.copyFrom(world, volume);
		WorldChange setResult = SetOperation.execute(world, volume, new BlockPattern(Blocks.AIR));
		WorldChange moveResult = cuboidVolumeBuffer.setAt(world, setPos, BlockMask.ANY);
		moveResult.update(setResult);

		if (shiftVolume) {
			volume.shift(shiftVector);
		}

		return moveResult;
	}
}
