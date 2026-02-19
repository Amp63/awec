package amp.awec.volume;

import amp.awec.util.BlockPos;
import amp.awec.util.BlockState;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.core.world.World;

import java.util.ArrayList;
import java.util.Iterator;

public class CopiedVolume {
	private final ArrayList<BlockState> blockBuffer;
	private final int dimX, dimY, dimZ;
	private BlockPos rootPos;

	public CopiedVolume(World world, BlockPos corner1, BlockPos corner2) {
		BlockVolumeIterator iterator = new BlockVolumeIterator(corner1, corner2);
		int area = iterator.getArea();
		rootPos = iterator.getCorner1();

		blockBuffer = new ArrayList<>();
		blockBuffer.ensureCapacity(area);

		int[] dimensions = iterator.getDimensions();
		dimX = dimensions[0];
		dimY = dimensions[1];
		dimZ = dimensions[2];

		while (iterator.hasNext()) {
			BlockPos copyPos = iterator.next();
			blockBuffer.add(new BlockState(world, copyPos));
		}
	}

	public void setAt(World world, BlockPos setPos) {
		BlockPos corner2 = new BlockPos(
			setPos.x + dimX - 1,
			setPos.y + dimY - 1,
			setPos.z + dimZ - 1
		);

		BlockVolumeIterator iterator = new BlockVolumeIterator(setPos, corner2);
		Iterator<BlockState> bufferIterator = blockBuffer.iterator();
		while (iterator.hasNext()) {
			BlockPos setBlockPos = iterator.next();
			BlockState blockState = bufferIterator.next();
			blockState.setNotify(world, setBlockPos);
		}
	}

	public BlockPos getRootPos() {
		return rootPos;
	}
}
