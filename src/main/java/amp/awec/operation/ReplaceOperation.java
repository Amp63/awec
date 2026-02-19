package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockState;
import amp.awec.util.CuboidVolume;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolumeIterator;
import net.minecraft.core.world.World;

public class ReplaceOperation {

	public static int doReplace(World world, CuboidVolume volume, BlockPattern targetPattern, BlockPattern replaceWithPattern) {
		CuboidVolumeIterator iterator = new CuboidVolumeIterator(volume);
		int changedBlocks = 0;

		while (iterator.hasNext()) {
			Vec3i setPos = iterator.next();
			BlockState replacedBlock = new BlockState(world, setPos);
			if (targetPattern.shouldReplace(replacedBlock)) {
				BlockState sampledBlock = replaceWithPattern.sample();
				if (sampledBlock != null) {
					sampledBlock.setNotify(world, setPos);
					changedBlocks++;
				}
			}
		}

		return changedBlocks;
	}
}
