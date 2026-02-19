package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockState;
import amp.awec.util.CuboidVolume;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolumeIterator;
import net.minecraft.core.world.World;

public class SetOperation {

	public static int doSet(World world, CuboidVolume volume, BlockPattern pattern) {
		CuboidVolumeIterator iterator = new CuboidVolumeIterator(volume);
		int changedBlocks = 0;

		while (iterator.hasNext()) {
			Vec3i setPos = iterator.next();
			BlockState sampledBlock = pattern.sample();
			if (sampledBlock != null) {
				sampledBlock.setNotify(world, setPos);
				changedBlocks++;
			}
		}

		return changedBlocks;
	}
}
