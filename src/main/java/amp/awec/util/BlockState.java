package amp.awec.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockState {
	public @Nullable Block<?> block;
	public int metadata = 0;
	public TileEntity tileEntity = null;

	public BlockState(@Nullable Block<?> block, int metadata, TileEntity tileEntity) {
		this.block = block;
		this.metadata = metadata;
		this.tileEntity = tileEntity;
	}

	public BlockState(@Nullable Block<?> block, int metadata) {
		this.block = block;
		this.metadata = metadata;
	}

	public BlockState(World world, BlockPos pos) {
		this.block = world.getBlock(pos.x, pos.y, pos.z);
		this.metadata = world.getBlockMetadata(pos.x, pos.y, pos.z);
		this.tileEntity = world.getTileEntity(pos.x, pos.y, pos.z);
	}

	public void setNotify(World world, BlockPos pos) {
		int blockId = 0;
		if (block != null) {
			blockId = block.id();
		}
		world.setBlockAndMetadataWithNotify(pos.x, pos.y, pos.z, blockId, metadata);

		if (tileEntity != null) {
			world.setTileEntity(pos.x, pos.y, pos.z, tileEntity);
		}
	}
}
