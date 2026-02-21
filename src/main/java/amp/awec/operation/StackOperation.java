package amp.awec.operation;

import amp.awec.volume.CuboidVolume;
import amp.awec.util.Vec3i;
import amp.awec.volume.CopiedVolume;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;

public class StackOperation {
	public static WorldChange execute(World world, CuboidVolume volume, int amount, Direction direction) {
		WorldChange result = new WorldChange();

		CopiedVolume copiedVolume = new CopiedVolume(world, volume);

		Vec3i currentSetPos = new Vec3i(volume.getMinCorner());
		Vec3i directionVec = new Vec3i(direction);
		Vec3i shiftVector = volume.getDim().componentMultiply(directionVec);

		for (int i = 0; i < amount; i++) {
			currentSetPos.addi(shiftVector);
			WorldChange setResult = copiedVolume.setAt(world, currentSetPos, true);
			result.update(setResult);
		}

		return result;
	}
}
