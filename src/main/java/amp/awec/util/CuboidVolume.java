package amp.awec.util;

public class CuboidVolume {
	private Vec3i corner1, corner2;
	private Vec3i minCorner, maxCorner;

	public CuboidVolume(Vec3i c1, Vec3i c2) {
		corner1 = c1;
		corner2 = c2;
		updateCorrectedCorners();
	}

	public void setCorner1(Vec3i pos) {
		corner1 = pos;
		updateCorrectedCorners();
	}

	public void setCorner2(Vec3i pos) {
		corner2 = pos;
		updateCorrectedCorners();
	}

	public boolean isComplete() {
		return (corner1 != null && corner2 != null);
	}

	private void updateCorrectedCorners() {
		if (!isComplete()) {
			return;
		}
		minCorner = new Vec3i(Math.min(corner1.x, corner2.x), Math.min(corner1.y, corner2.y), Math.min(corner1.z, corner2.z));
		maxCorner = new Vec3i(Math.max(corner1.x, corner2.x), Math.max(corner1.y, corner2.y), Math.max(corner1.z, corner2.z));
	}

	public Vec3i getMinCorner() {
		return minCorner;
	}

	public Vec3i getMaxCorner() {
		return maxCorner;
	}
}
