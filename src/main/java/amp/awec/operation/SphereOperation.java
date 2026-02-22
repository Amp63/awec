package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockState;
import amp.awec.volume.CuboidVolume;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolumeIterator;
import net.minecraft.core.world.World;

public class SphereOperation {
	public static WorldChange execute(World world, Vec3i center, BlockPattern pattern, int radius) {
		WorldChange result = new WorldChange();

		final int radiusSquared = radius * radius;
		Vec3i setPos = new Vec3i();

		for (int x = center.x-radius; x <= center.y+radius; x++) {
			int dxSquared = (x - center.x) * (x - center.x);
			if (dxSquared > radiusSquared) {
				continue;
			}

			for (int y = center.y-radius; y <= center.y+radius; y++) {
				int dySquared = (y - center.y) * (y - center.y);
				if (dxSquared + dySquared > radiusSquared) {
					continue;
				}

				for (int z = center.z-radius; z <= center.z+radius; z++) {
					int dzSquared = (z - center.z) * (z - center.z);
					int distSquared = dxSquared + dySquared + dzSquared;
					if (distSquared > radiusSquared) {
						continue;
					}
					BlockState sampledBlock = pattern.sample();
					if (sampledBlock != null) {
						setPos.set(x, y, z);
						BlockState oldBlock = sampledBlock.setNotify(world, setPos);
						result.putChange(setPos, oldBlock);
					}
				}
			}
		}

		return result;
	}
}
