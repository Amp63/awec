package amp.awec.util;

import amp.awec.BlockPos;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;

public class BlockState {
	public Block<?> block;
	public int metadata = 0;
	public TileEntity tileEntity = null;

	public BlockState(Block<?> block, int metadata, TileEntity tileEntity) {
		this.block = block;
		this.metadata = metadata;
		this.tileEntity = tileEntity;
	}

	public BlockState(Block<?> block, int metadata) {
		this.block = block;
		this.metadata = metadata;
	}

	public void set(World world, BlockPos pos) {
		world.setBlockAndMetadataWithNotify(pos.x, pos.y, pos.z, block.id(), metadata);

		if (tileEntity != null) {
			world.setTileEntity(pos.x, pos.y, pos.z, tileEntity);
		}
	}
}
