package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.util.CuboidVolume;
import amp.awec.util.Vec3i;
import net.minecraft.core.world.World;

public class WallsOperation {

	public static int doWalls(World world, CuboidVolume volume, BlockPattern pattern, int thickness) {
		Vec3i minCorner = volume.getMinCorner();
		Vec3i maxCorner = volume.getMaxCorner();

		int minHorizontalDim = Math.min(volume.getDimX(), volume.getDimZ());
		thickness = Math.min(minHorizontalDim/2, thickness);

		int insideLeftZ = minCorner.z+thickness-1;
		int insideRightZ = maxCorner.z-thickness+1;
		int insideFrontX = minCorner.x+thickness-1;
		int insideBackX = maxCorner.x-thickness+1;

		CuboidVolume[] volumes = {
			new CuboidVolume(minCorner, new Vec3i(maxCorner.x, maxCorner.y, insideLeftZ)),
			new CuboidVolume(new Vec3i(minCorner.x, minCorner.y, insideRightZ), maxCorner),
			new CuboidVolume(new Vec3i(minCorner.x, minCorner.y, insideLeftZ), new Vec3i(insideFrontX, maxCorner.y, insideRightZ)),
			new CuboidVolume(new Vec3i(insideBackX, minCorner.y, insideLeftZ), new Vec3i(maxCorner.x, maxCorner.y, insideRightZ))
		};

		int changedBlocks = 0;
		for (CuboidVolume wallVolume : volumes) {
			changedBlocks += SetOperation.doSet(world, wallVolume, pattern);
		}

		return changedBlocks;
	}
}
