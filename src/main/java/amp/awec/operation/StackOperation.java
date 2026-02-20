package amp.awec.operation;

import amp.awec.volume.CuboidVolume;
import amp.awec.util.Vec3i;
import amp.awec.volume.CopiedVolume;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;

public class StackOperation {
	public static OperationResult execute(World world, CuboidVolume volume, int amount, Direction direction) {
		OperationResult result = new OperationResult();
		CopiedVolume copiedVolume = new CopiedVolume(world, volume);

		Vec3i currentSetPos = new Vec3i(volume.getMinCorner());
		Vec3i directionVec = new Vec3i(
			direction.getOffsetX(),
			direction.getOffsetY(),
			direction.getOffsetZ()
		);
		Vec3i shiftVector = volume.getDim().componentMultiply(directionVec);

		// Calculate volume that will be changed
		CuboidVolume overwrittenVolume = new CuboidVolume(
			volume.getMinCorner().add(shiftVector),
			volume.getMaxCorner().add(shiftVector.scale(amount-1))
		);
		result.copyPreviousVolume(world, overwrittenVolume);

		for (int i = 0; i < amount; i++) {
			currentSetPos.addi(shiftVector);
			result.changedBlocks += copiedVolume.setAt(world, currentSetPos, false).changedBlocks;
		}

		return result;
	}
}
