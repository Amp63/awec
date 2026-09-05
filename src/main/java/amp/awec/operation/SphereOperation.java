package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockState;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

public class SphereOperation {
	public static WorldChange execute(World world, TilePos center, BlockPattern pattern, int radius) {
		WorldChange result = new WorldChange();

		final int radiusSquared = radius * radius;
		TilePos setPos = new TilePos();

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
						BlockState oldBlock = sampledBlock.set(world, setPos);
						result.putChange(setPos, oldBlock);
					}
				}
			}
		}

		return result;
	}
}
