package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockState;
import amp.awec.util.Vec3i;
import net.minecraft.core.world.World;

public class HollowSphereOperation {
	public static WorldChange execute(World world, Vec3i center, BlockPattern pattern, int radius) {
		WorldChange result = new WorldChange();

		final int cx = center.x;
		final int cy = center.y;
		final int cz = center.z;

		final int radiusSquared = radius * radius;
		Vec3i setPos = new Vec3i();
		int[] neighbors = new int[6];

		for (int x = cx-radius; x <= center.y+radius; x++) {
			int dxSquared = (x - cx) * (x - cx);
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

					neighbors[0] = (x+1-cx)*(x+1-cx) + dySquared + dzSquared;
					neighbors[1] = (x-1-cx)*(x-1-cx) + dySquared + dzSquared;
					neighbors[2] = dxSquared + (y+1-center.y)*(y+1-center.y) + dzSquared;
					neighbors[3] = dxSquared + (y-1-cy)*(y-1-cy) + dzSquared;
					neighbors[4] = dxSquared + dySquared + (z+1-cz)*(z+1-cz);
					neighbors[5] = dxSquared + dySquared + (z-1-cz)*(z-1-cz);

					boolean onSphere = false;
					for (int neighbor : neighbors) {
						if (neighbor > radiusSquared) {
							onSphere = true;
							break;
						}
					}

					if (onSphere) {
						BlockState sampledBlock = pattern.sample();
						if (sampledBlock != null) {
							setPos.set(x, y, z);
							BlockState oldBlock = sampledBlock.setNotify(world, setPos);
							result.putChange(setPos, oldBlock);
						}
					}
				}
			}
		}

		return result;
	}
}
