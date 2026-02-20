package amp.awec.volume;


import amp.awec.util.Vec3i;

public class CuboidVolumeIterator {
	private final Vec3i rootPos;
	private final int dimX, dimY, dimZ, area;
	private int currentIndex;
	private final Vec3i currentPos;

	public CuboidVolumeIterator(CuboidVolume volume) {
		currentIndex = 0;
		rootPos = volume.getMinCorner();
		dimX = volume.getDimX();
		dimY = volume.getDimY();
		dimZ = volume.getDimZ();
		area = dimX * dimY * dimZ;
		currentPos = new Vec3i(0, 0, 0);
	}

	public Vec3i next() {
		int x = currentIndex % dimX;
		int y = currentIndex / dimX % dimY;
		int z = currentIndex / (dimX * dimY);
		currentIndex++;
		currentPos.set(rootPos.x + x, rootPos.y + y, rootPos.z + z);
		return currentPos;
	}

	public boolean hasNext() {
		return currentIndex < area;
	}

	public int getArea() {
		return area;
	}

	public int[] getDimensions() {
		return new int[] {dimX, dimY, dimZ};
	}
}
