package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockState;
import amp.awec.util.Vec3i;
import net.minecraft.core.world.World;

public class CylinderOperation {
	public static WorldChange execute(World world, Vec3i center, BlockPattern pattern, int radius, int height) {
		WorldChange result = new WorldChange();

		final int radiusSquared = radius * radius;
		Vec3i setPos = new Vec3i();

		int cx = center.x;
		int cy = center.y;
		int cz = center.z;

		for (int y = cy; y < cy + height; y++) {
			for (int x = cx - radius; x <= cx + radius; x++) {
				int dxSquared = (x - cx) * (x - cx);
				if (dxSquared > radiusSquared) {
					continue;
				}

				for (int z = cz - radius; z <= cz + radius; z++) {
					int dzSquared = (z - cz) * (z - cz);
					if (dxSquared + dzSquared > radiusSquared) {
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
