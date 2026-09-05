package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockState;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

public class HollowCylinderOperation {
	public static WorldChange execute(World world, TilePos center, BlockPattern pattern, int radius, int height) {
		WorldChange result = new WorldChange();

		final int radiusSquared = radius * radius;
		TilePos setPos = new TilePos();

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

					boolean onSurface =
						(x+1-cx)*(x+1-cx) + dzSquared > radiusSquared ||
						(x-1-cx)*(x-1-cx) + dzSquared > radiusSquared ||
						dxSquared + (z+1-cz)*(z+1-cz) > radiusSquared ||
						dxSquared + (z-1-cz)*(z-1-cz) > radiusSquared;


					if (onSurface) {
						BlockState sampledBlock = pattern.sample();
						if (sampledBlock != null) {
							setPos.set(x, y, z);
							BlockState oldBlock = sampledBlock.set(world, setPos);
							result.putChange(setPos, oldBlock);
						}
					}
				}
			}
		}

		return result;
	}
}
