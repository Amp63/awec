package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockChange;
import amp.awec.util.BlockState;
import amp.awec.volume.CuboidVolume;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolumeIterator;
import net.minecraft.core.world.World;

public class SetOperation {

	public static WorldChange execute(World world, CuboidVolume volume, BlockPattern pattern) {
		return execute(world, volume, pattern, true);
	}

	public static WorldChange execute(World world, CuboidVolume volume, BlockPattern pattern, boolean copyPrevious) {
		WorldChange result = new WorldChange();

		CuboidVolumeIterator iterator = new CuboidVolumeIterator(volume);

		while (iterator.hasNext()) {
			Vec3i setPos = iterator.next();
			BlockState sampledBlock = pattern.sample();
			if (sampledBlock != null) {
				BlockChange blockChange = sampledBlock.setNotify(world, setPos);
				result.addChange(blockChange);
			}
		}

		return result;
	}
}
