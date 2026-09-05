package amp.awec.operation;

import amp.awec.pattern.BlockMask;
import amp.awec.pattern.BlockPattern;
import amp.awec.volume.CuboidVolume;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

public class WallsOperation {

	public static WorldChange execute(World world, CuboidVolume volume, BlockPattern pattern, BlockMask mask, int thickness) {
		WorldChange result = new WorldChange();

		TilePos minCorner = volume.getMinCorner();
		TilePos maxCorner = volume.getMaxCorner();

		int minHorizontalDim = Math.min(volume.getDimX(), volume.getDimZ());
		thickness = Math.min(minHorizontalDim/2, thickness);

		CuboidVolume[] volumes = getWallVolumes(thickness, minCorner, maxCorner);

		for (CuboidVolume wallVolume : volumes) {
			WorldChange setResult = SetOperation.execute(world, wallVolume, pattern, mask);
			result.update(setResult);
		}

		return result;
	}

	public static CuboidVolume [] getWallVolumes(int thickness, TilePos minCorner, TilePos maxCorner) {
		int insideLeftZ = Math.max(minCorner.z+thickness-1, minCorner.z);
		int insideRightZ = Math.min(maxCorner.z-thickness+1, maxCorner.z);
		int insideFrontX = Math.max(minCorner.x+thickness-1, minCorner.x);
		int insideBackX = Math.min(maxCorner.x-thickness+1, maxCorner.x);

		CuboidVolume left = new CuboidVolume(minCorner, new TilePos(maxCorner.x, maxCorner.y, insideLeftZ));
		CuboidVolume right = new CuboidVolume(new TilePos(minCorner.x, minCorner.y, insideRightZ), maxCorner);
		CuboidVolume front = new CuboidVolume(new TilePos(minCorner.x, minCorner.y, insideLeftZ+1), new TilePos(insideFrontX, maxCorner.y, insideRightZ-1));
		CuboidVolume back = new CuboidVolume(new TilePos(insideBackX, minCorner.y, insideLeftZ+1), new TilePos(maxCorner.x, maxCorner.y, insideRightZ-1));

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
