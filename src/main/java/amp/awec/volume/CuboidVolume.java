package amp.awec.volume;

import net.minecraft.core.world.pos.TilePos;
import org.joml.Vector3i;

public class CuboidVolume {
	private TilePos corner1, corner2;
	private TilePos minCorner, maxCorner;

	public CuboidVolume(TilePos c1, TilePos c2) {
		corner1 = c1;
		corner2 = c2;
		updateCorrectedCorners();
	}

	public TilePos getCorner1() {
		return corner1;
	}

	public TilePos getCorner2() {
		return corner2;
	}

	public void setCorner1(TilePos pos) {
		corner1 = pos;
		updateCorrectedCorners();
	}

	public void setCorner2(TilePos pos) {
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
		minCorner = new TilePos(Math.min(corner1.x, corner2.x), Math.min(corner1.y, corner2.y), Math.min(corner1.z, corner2.z));
		maxCorner = new TilePos(Math.max(corner1.x, corner2.x), Math.max(corner1.y, corner2.y), Math.max(corner1.z, corner2.z));
	}

	public TilePos getMinCorner() {
		return minCorner;
	}

	public TilePos getMaxCorner() {
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

	public Vector3i getDim() {
		return new Vector3i(getDimX(), getDimY(), getDimZ());
	}

	public boolean shift(Vector3i shiftVector) {
		if (!isComplete()) {
			return false;
		}

		corner1 = corner1.add(shiftVector);
		corner2 = corner2.add(shiftVector);
		updateCorrectedCorners();

		return true;
	}

	public boolean expand(Vector3i expandVector, int amount) {
		if (!isComplete()) {
			return false;
		}

		Vector3i scaledVector = expandVector.mul(amount);

		Vector3i minCornerExpand = new Vector3i(
			expandVector.x < 0 ? scaledVector.x : 0,
			expandVector.y < 0 ? scaledVector.y : 0,
			expandVector.z < 0 ? scaledVector.z : 0
		);
		Vector3i maxCornerExpand = new Vector3i(
			expandVector.x > 0 ? scaledVector.x : 0,
			expandVector.y > 0 ? scaledVector.y : 0,
			expandVector.z > 0 ? scaledVector.z : 0
		);

		TilePos newMin = minCorner.add(minCornerExpand);
		TilePos newMax = maxCorner.add(maxCornerExpand);

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
