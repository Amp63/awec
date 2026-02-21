package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.Vec3i;
import amp.awec.volume.CopiedVolume;
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

		CopiedVolume copiedVolume = new CopiedVolume(world, volume);
		WorldChange setResult = SetOperation.execute(world, volume, new BlockPattern((Block<?>) null));
		WorldChange moveResult = copiedVolume.setAt(world, setPos, true);
		moveResult.update(setResult);

		return moveResult;
	}
}
