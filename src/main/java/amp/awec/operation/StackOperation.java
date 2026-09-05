package amp.awec.operation;

import amp.awec.pattern.BlockMask;
import amp.awec.util.DirectionHelper;
import amp.awec.volume.CuboidVolume;
import amp.awec.volume.CuboidVolumeBuffer;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.joml.Vector3i;

public class StackOperation {
	public static WorldChange execute(World world, CuboidVolume volume, int amount, Direction direction) {
		WorldChange result = new WorldChange();

		CuboidVolumeBuffer cuboidVolumeBuffer = CuboidVolumeBuffer.copyFrom(world, volume);

		TilePos currentSetPos = new TilePos(volume.getMinCorner());
		Vector3i directionVec = DirectionHelper.getVec3i(direction);
		Vector3i shiftVector = volume.getDim().mul(directionVec);

		for (int i = 0; i < amount; i++) {
			currentSetPos.add(shiftVector);
			WorldChange setResult = cuboidVolumeBuffer.setAt(world, currentSetPos, BlockMask.ANY);
			result.update(setResult);
		}

		return result;
	}
}
