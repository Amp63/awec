package amp.awec;


public class BlockVolumeIterator {
	private final BlockPos corner1, corner2;
	private int currentIndex;
	private final int area;

	public BlockVolumeIterator(BlockPos c1, BlockPos c2) {
		corner1 = new BlockPos(Math.min(c1.x, c2.x), Math.min(c1.y, c2.y), Math.min(c1.z, c2.z));
		corner2 = new BlockPos(Math.max(c1.x, c2.x), Math.max(c1.y, c2.y), Math.max(c1.z, c2.z));
		currentIndex = 0;
		area = (corner2.x+1-corner1.x) * (corner2.y+1-corner1.y) * (corner2.z+1-corner1.z);
	}

	public BlockPos next() {
		int xdiff = corner2.x+1 - corner1.x;
		int ydiff = corner2.y+1 - corner1.y;
		int x = currentIndex % xdiff;
		int y = currentIndex / xdiff % ydiff;
		int z = currentIndex / (xdiff * ydiff);
		currentIndex++;
		return new BlockPos(corner1.x + x, corner1.y + y, corner1.z + z);
	}

	public boolean hasNext() {
		return currentIndex < area;
	}
}
