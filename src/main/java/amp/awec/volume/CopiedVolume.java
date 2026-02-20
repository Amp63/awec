package amp.awec.volume;

import amp.awec.util.Vec3i;
import amp.awec.util.BlockState;
import amp.awec.util.CuboidVolume;
import net.minecraft.core.world.World;

import java.util.ArrayList;
import java.util.Iterator;

public class CopiedVolume {
	private final ArrayList<BlockState> blockBuffer;
	private final int dimX, dimY, dimZ;

	public CopiedVolume(World world, CuboidVolume volume) {
		CuboidVolumeIterator iterator = new CuboidVolumeIterator(volume);
		int area = iterator.getArea();

		blockBuffer = new ArrayList<>();
		blockBuffer.ensureCapacity(area);

		int[] dimensions = iterator.getDimensions();
		dimX = dimensions[0];
		dimY = dimensions[1];
		dimZ = dimensions[2];

		while (iterator.hasNext()) {
			Vec3i copyPos = iterator.next();
			blockBuffer.add(new BlockState(world, copyPos));
		}
	}

	public int setAt(World world, Vec3i setPos) {
		Vec3i corner2 = new Vec3i(
			setPos.x + dimX - 1,
			setPos.y + dimY - 1,
			setPos.z + dimZ - 1
		);

		CuboidVolumeIterator iterator = new CuboidVolumeIterator(new CuboidVolume(setPos, corner2));
		Iterator<BlockState> bufferIterator = blockBuffer.iterator();
		int changedBlocks = 0;
		while (iterator.hasNext()) {
			Vec3i setBlockPos = iterator.next();
			BlockState blockState = bufferIterator.next();
			blockState.setNotify(world, setBlockPos);
			changedBlocks++;
		}

		return changedBlocks;
	}
}
