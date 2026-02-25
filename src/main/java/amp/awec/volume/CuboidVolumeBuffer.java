package amp.awec.volume;

import amp.awec.operation.WorldChange;
import amp.awec.pattern.BlockMask;
import amp.awec.util.BlockFlipper;
import amp.awec.util.Vec3i;
import amp.awec.util.BlockState;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class CuboidVolumeBuffer {
	private final BlockState[] blockBuffer;
	private final int dimX, dimY, dimZ;

	public CuboidVolumeBuffer(int dimX, int dimY, int dimZ) {
		this.dimX = dimX;
		this.dimY = dimY;
		this.dimZ = dimZ;

		blockBuffer = new BlockState[dimX * dimY * dimZ];
	}

	public static CuboidVolumeBuffer copyFrom(World world, CuboidVolume volume) {
		CuboidVolumeIterator iterator = new CuboidVolumeIterator(volume);
		int[] dimensions = iterator.getDimensions();
		int dimX = dimensions[0];
		int dimY = dimensions[1];
		int dimZ = dimensions[2];

		CuboidVolumeBuffer volumeBuffer = new CuboidVolumeBuffer(dimX, dimY, dimZ);

		int index = 0;
		while (iterator.hasNext()) {
			Vec3i copyPos = iterator.next();
			volumeBuffer.blockBuffer[index] = new BlockState(world, copyPos);
			index++;
		}

		return volumeBuffer;
	}

	public WorldChange setAt(World world, Vec3i setPos, @Nullable BlockMask mask) {
		Vec3i corner2 = new Vec3i(
			setPos.x + dimX - 1,
			setPos.y + dimY - 1,
			setPos.z + dimZ - 1
		);

		CuboidVolume setVolume = new CuboidVolume(setPos, corner2);

		WorldChange result = new WorldChange();

		CuboidVolumeIterator iterator = new CuboidVolumeIterator(setVolume);
		int index = 0;
		while (iterator.hasNext()) {
			Vec3i setBlockPos = iterator.next();
			BlockState setBlock = blockBuffer[index];
			BlockState oldBlock = setBlock.setNotify(world, setBlockPos, mask);
			result.putChange(setBlockPos, oldBlock);
			index++;
		}

		return result;
	}

	public BlockState[] getBlockBuffer() {
		return blockBuffer;
	}

	public Vec3i getDim() {
		return new Vec3i(dimX, dimY, dimZ);
	}

	@FunctionalInterface
	private interface IndexFunction {
		int apply(int x, int y, int z);
	}

	private void flipAxis(Vec3i corner2, Vec3i flipVector, IndexFunction currentIndexFn, IndexFunction swapIndexFn) {
		CuboidVolumeIterator iterator = new CuboidVolumeIterator(new CuboidVolume(Vec3i.ZERO, corner2));
		while (iterator.hasNext()) {
			Vec3i pos = iterator.next();
			int currentIndex = currentIndexFn.apply(pos.x, pos.y, pos.z);
			int swapIndex = swapIndexFn.apply(pos.x, pos.y, pos.z);

			BlockState temp = blockBuffer[currentIndex];
			blockBuffer[currentIndex] = blockBuffer[swapIndex];
			blockBuffer[swapIndex] = temp;

			BlockFlipper.flip(blockBuffer[currentIndex], flipVector);
			if (blockBuffer[currentIndex] != blockBuffer[swapIndex]) {
				// Prevent double flipping
				BlockFlipper.flip(blockBuffer[swapIndex], flipVector);
			}
		}
	}

	public void flip(Vec3i flipVector) {
		flipVector.absi();

		if (flipVector.x == 1) {
			flipAxis(
				new Vec3i((dimX+1)/2-1, dimY-1, dimZ-1),
				flipVector,
				(x, y, z) -> x + z*dimX + y*dimX*dimZ,
				(x, y, z) -> (dimX-1 - x) + z*dimX + y*dimX*dimZ
			);
		}
		if (flipVector.y == 1) {
			flipAxis(
				new Vec3i(dimX-1, (dimY+1)/2-1, dimZ-1),
				flipVector,
				(x, y, z) -> x + z*dimX + y*dimX*dimZ,
				(x, y, z) -> x + z*dimX + (dimY-1 - y)*dimX*dimZ
			);
		}
		if (flipVector.z == 1) {
			flipAxis(
				new Vec3i(dimX-1, dimY-1, (dimZ+1)/2-1),
				flipVector,
				(x, y, z) -> x + z*dimX + y*dimX*dimZ,
				(x, y, z) -> x + (dimZ-1 - z)*dimX + y*dimX*dimZ
			);
		}
	}
}
