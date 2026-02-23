package amp.awec.volume;

import amp.awec.operation.WorldChange;
import amp.awec.util.Vec3i;
import amp.awec.util.BlockState;
import net.minecraft.core.world.World;

import java.util.ArrayList;
import java.util.Iterator;

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

	public WorldChange setAt(World world, Vec3i setPos, boolean trackChanges) {
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
			BlockState oldBlock = blockBuffer[index].setNotify(world, setBlockPos);
			if (trackChanges) {
				result.putChange(setBlockPos, oldBlock);
			}
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
}
