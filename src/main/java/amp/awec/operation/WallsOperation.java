package amp.awec.operation;

import amp.awec.pattern.BlockPattern;
import amp.awec.volume.CuboidVolume;
import amp.awec.util.Vec3i;
import net.minecraft.core.world.World;
import org.jspecify.annotations.NonNull;

public class WallsOperation {

	public static WorldChange execute(World world, CuboidVolume volume, BlockPattern pattern, int thickness) {
		WorldChange result = new WorldChange();

		Vec3i minCorner = volume.getMinCorner();
		Vec3i maxCorner = volume.getMaxCorner();

		int minHorizontalDim = Math.min(volume.getDimX(), volume.getDimZ());
		thickness = Math.min(minHorizontalDim/2, thickness);

		CuboidVolume[] volumes = getWallVolumes(thickness, minCorner, maxCorner);

		int changedBlocks = 0;
		for (CuboidVolume wallVolume : volumes) {
			WorldChange setResult = SetOperation.execute(world, wallVolume, pattern, false);
			result.update(setResult);
		}

		return result;
	}

	private static CuboidVolume @NonNull [] getWallVolumes(int thickness, Vec3i minCorner, Vec3i maxCorner) {
		int insideLeftZ = minCorner.z+thickness-1;
		int insideRightZ = maxCorner.z-thickness+1;
		int insideFrontX = minCorner.x+thickness-1;
		int insideBackX = maxCorner.x-thickness+1;

		return new CuboidVolume[]{
			new CuboidVolume(minCorner, new Vec3i(maxCorner.x, maxCorner.y, insideLeftZ)),
			new CuboidVolume(new Vec3i(minCorner.x, minCorner.y, insideRightZ), maxCorner),
			new CuboidVolume(new Vec3i(minCorner.x, minCorner.y, insideLeftZ), new Vec3i(insideFrontX, maxCorner.y, insideRightZ)),
			new CuboidVolume(new Vec3i(insideBackX, minCorner.y, insideLeftZ), new Vec3i(maxCorner.x, maxCorner.y, insideRightZ))
		};
	}
}
