package amp.awec.volume;


import amp.awec.util.Vec3i;
import amp.awec.util.CuboidVolume;

public class CuboidVolumeIterator {
	private final Vec3i rootPos;
	private int currentIndex;
	private final int dimX, dimY, dimZ, area;

	public CuboidVolumeIterator(CuboidVolume volume) {
		currentIndex = 0;
		rootPos = volume.getMinCorner();
		dimX = volume.getDimX();
		dimY = volume.getDimY();
		dimZ = volume.getDimZ();
		area = dimX * dimY * dimZ;
	}

	public Vec3i next() {
		int x = currentIndex % dimX;
		int y = currentIndex / dimX % dimY;
		int z = currentIndex / (dimX * dimY);
		currentIndex++;
		return new Vec3i(rootPos.x + x, rootPos.y + y, rootPos.z + z);
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
