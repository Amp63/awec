package amp.awec.volume;


import amp.awec.util.BlockPos;

public class BlockVolumeIterator {
	private final BlockPos corner1, corner2;
	private int currentIndex;
	private final int dimX, dimY, dimZ, area;

	public BlockVolumeIterator(BlockPos c1, BlockPos c2) {
		corner1 = new BlockPos(Math.min(c1.x, c2.x), Math.min(c1.y, c2.y), Math.min(c1.z, c2.z));
		corner2 = new BlockPos(Math.max(c1.x, c2.x), Math.max(c1.y, c2.y), Math.max(c1.z, c2.z));
		currentIndex = 0;
		dimX = corner2.x + 1 - corner1.x;
		dimY = corner2.y + 1 - corner1.y;
		dimZ = corner2.z + 1 - corner1.z;
		area = dimX * dimY * dimZ;
	}

	public BlockPos next() {
		int x = currentIndex % dimX;
		int y = currentIndex / dimX % dimY;
		int z = currentIndex / (dimX * dimY);
		currentIndex++;
		return new BlockPos(corner1.x + x, corner1.y + y, corner1.z + z);
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

	public BlockPos getCorner1() {
		return corner1;
	}
}
