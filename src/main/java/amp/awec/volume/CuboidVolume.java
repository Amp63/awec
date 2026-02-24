package amp.awec.volume;

import amp.awec.util.Vec3i;

public class CuboidVolume {
	private Vec3i corner1, corner2;
	private Vec3i minCorner, maxCorner;

	public CuboidVolume(Vec3i c1, Vec3i c2) {
		corner1 = c1;
		corner2 = c2;
		updateCorrectedCorners();
	}

	public Vec3i getCorner1() {
		return corner1;
	}

	public Vec3i getCorner2() {
		return corner2;
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

	public int getDimX() {
		if (!isComplete()) {
			return -1;
		}
		return maxCorner.x - minCorner.x + 1;
	}

	public int getDimY() {
		if (!isComplete()) {
			return -1;
		}
		return maxCorner.y - minCorner.y + 1;
	}

	public int getDimZ() {
		if (!isComplete()) {
			return -1;
		}
		return maxCorner.z - minCorner.z + 1;
	}

	public Vec3i getDim() {
		return new Vec3i(getDimX(), getDimY(), getDimZ());
	}

	public boolean shift(Vec3i shiftVector) {
		if (!isComplete()) {
			return false;
		}

		corner1.addi(shiftVector);
		corner2.addi(shiftVector);
		updateCorrectedCorners();

		return true;
	}

	public boolean expand(Vec3i expandVector, int amount) {
		if (!isComplete()) {
			return false;
		}

		Vec3i scaledVector = expandVector.scale(amount);

		Vec3i minCornerExpand = new Vec3i(
			expandVector.x < 0 ? scaledVector.x : 0,
			expandVector.y < 0 ? scaledVector.y : 0,
			expandVector.z < 0 ? scaledVector.z : 0
		);
		Vec3i maxCornerExpand = new Vec3i(
			expandVector.x > 0 ? scaledVector.x : 0,
			expandVector.y > 0 ? scaledVector.y : 0,
			expandVector.z > 0 ? scaledVector.z : 0
		);

		Vec3i newMin = minCorner.add(minCornerExpand);
		Vec3i newMax = maxCorner.add(maxCornerExpand);

		newMin.x = Math.min(newMin.x, maxCorner.x);
		newMin.y = Math.min(newMin.y, maxCorner.y);
		newMin.z = Math.min(newMin.z, maxCorner.z);
		newMax.x = Math.max(newMax.x, minCorner.x);
		newMax.y = Math.max(newMax.y, minCorner.y);
		newMax.z = Math.max(newMax.z, minCorner.z);

		corner1.set(newMin);
		corner2.set(newMax);

		updateCorrectedCorners();

		return true;
	}
}
