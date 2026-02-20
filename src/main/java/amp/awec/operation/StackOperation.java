package amp.awec.operation;

import amp.awec.WorldEditMod;
import amp.awec.util.CuboidVolume;
import amp.awec.util.Vec3i;
import amp.awec.volume.CopiedVolume;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;

public class StackOperation {
	public static int doStack(World world, CuboidVolume volume, int amount, Direction direction) {
		CopiedVolume copiedVolume = new CopiedVolume(world, volume);
		Vec3i currentSetPos = new Vec3i(volume.getMinCorner());
		Vec3i directionVec = new Vec3i(
			direction.getOffsetX(),
			direction.getOffsetY(),
			direction.getOffsetZ()
		);
		Vec3i shiftVector = volume.getDim().componentMultiply(directionVec);

		int changedBlocks = 0;
		for (int i = 0; i < amount; i++) {
			currentSetPos.addi(shiftVector);
			changedBlocks += copiedVolume.setAt(world, currentSetPos);
		}

		return changedBlocks;
	}
}
