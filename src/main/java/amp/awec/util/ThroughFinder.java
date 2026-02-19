package amp.awec.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class ThroughFinder {
	public static @Nullable Vec3 findSpace(World world, Vec3 startPos, Vec3 direction, int wallCount,
										   double maxRayDistance, double maxThruDistance, double marchDistance) {
		Vec3 scaledDirection = direction.scale(maxRayDistance);
		Vec3 furthestPoint = startPos.add(scaledDirection.x, scaledDirection.y, scaledDirection.z);
		HitResult hit = world.checkBlockCollisionBetweenPoints(startPos, furthestPoint, false, false, false);
		if (hit == null) {
			return null;
		}

		Vec3 currentPos = hit.location;
		Vec3 marchVector = direction.scale(marchDistance);

		Vec3 lastValidPos = null;
		boolean lastBlockIsSolid = true;
		int wallsHit = 0;

		int maxSteps = (int) (maxThruDistance / marchDistance);
		for (int i = 0; i < maxSteps; i++) {
			currentPos.x += marchVector.x;
			currentPos.y += marchVector.y;
			currentPos.z += marchVector.z;

			Block<?> block = world.getBlock(
				(int) Math.floor(currentPos.x),
				(int) Math.floor(currentPos.y),
				(int) Math.floor(currentPos.z)
			);

			boolean blockIsSolid = block != null && block.getMaterial().isSolid();

			if (lastBlockIsSolid && !blockIsSolid) {
				// Block changed from solid to nonsolid
				lastValidPos = Vec3.getTempVec3(currentPos.x, currentPos.y, currentPos.z);
				wallsHit++;
				if (wallsHit >= wallCount) {
					break;
				}
			}

			lastBlockIsSolid = blockIsSolid;
		}

		return lastValidPos;
	}
}
