package amp.awec.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ThroughFinder {
	public static @Nullable Vector3dc findSpace(World world, Vector3d startPos, Vector3d direction, int wallCount,
										   double maxRayDistance, double maxThruDistance, double marchDistance) {
		Vector3d scaledDirection = new Vector3d(direction).mul(maxRayDistance);
		Vector3d furthestPoint = new Vector3d(startPos).add(scaledDirection);
		HitResult hit = world.checkBlockCollisionBetweenPoints(startPos, furthestPoint);
		if (!(hit instanceof HitResult.Tile)) {
			return null;
		}

		Vector3d currentPos = new Vector3d(hit.location);
		Vector3dc marchVector = direction.mul(marchDistance);

		Vector3d lastValidPos = null;
		boolean lastBlockIsSolid = true;
		int wallsHit = 0;

		int maxSteps = (int) (maxThruDistance / marchDistance);
		for (int i = 0; i < maxSteps; i++) {
			currentPos.add(marchVector);

			Block<?> block = world.getBlockType(new TilePos(currentPos));

			boolean blockIsSolid = block.getMaterial().isSolid();

			if (lastBlockIsSolid && !blockIsSolid) {
				// Block changed from solid to nonsolid
				lastValidPos = new Vector3d(currentPos);
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
