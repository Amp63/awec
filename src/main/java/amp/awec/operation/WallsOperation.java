package amp.awec.operation;

import amp.awec.pattern.BlockMask;
import amp.awec.pattern.BlockPattern;
import amp.awec.volume.CuboidVolume;
import amp.awec.util.Vec3i;
import net.minecraft.core.world.World;
import org.jspecify.annotations.NonNull;

public class WallsOperation {

	public static WorldChange execute(World world, CuboidVolume volume, BlockPattern pattern, BlockMask mask, int thickness) {
		WorldChange result = new WorldChange();

		Vec3i minCorner = volume.getMinCorner();
		Vec3i maxCorner = volume.getMaxCorner();

		int minHorizontalDim = Math.min(volume.getDimX(), volume.getDimZ());
		thickness = Math.min(minHorizontalDim/2, thickness);

		CuboidVolume[] volumes = getWallVolumes(thickness, minCorner, maxCorner);

		for (CuboidVolume wallVolume : volumes) {
			WorldChange setResult = SetOperation.execute(world, wallVolume, pattern, mask);
			result.update(setResult);
		}

		return result;
	}

	public static CuboidVolume @NonNull [] getWallVolumes(int thickness, Vec3i minCorner, Vec3i maxCorner) {
		int insideLeftZ = Math.max(minCorner.z+thickness-1, minCorner.z);
		int insideRightZ = Math.min(maxCorner.z-thickness+1, maxCorner.z);
		int insideFrontX = Math.max(minCorner.x+thickness-1, minCorner.x);
		int insideBackX = Math.min(maxCorner.x-thickness+1, maxCorner.x);

		CuboidVolume left = new CuboidVolume(minCorner, new Vec3i(maxCorner.x, maxCorner.y, insideLeftZ));
		CuboidVolume right = new CuboidVolume(new Vec3i(minCorner.x, minCorner.y, insideRightZ), maxCorner);
		CuboidVolume front = new CuboidVolume(new Vec3i(minCorner.x, minCorner.y, insideLeftZ+1), new Vec3i(insideFrontX, maxCorner.y, insideRightZ-1));
		CuboidVolume back = new CuboidVolume(new Vec3i(insideBackX, minCorner.y, insideLeftZ+1), new Vec3i(maxCorner.x, maxCorner.y, insideRightZ-1));

		int xdiff = maxCorner.x - minCorner.x;
		int zdiff = maxCorner.z - minCorner.z;

		if (xdiff == 0) {
			return new CuboidVolume[] {left, right, front};
		}
		if (zdiff == 0) {
			return new CuboidVolume[] {left};
		}
		if (zdiff == 1) {
			return new CuboidVolume[] {left, right};
		}

		return new CuboidVolume[] {left, right, front, back};
	}
}
