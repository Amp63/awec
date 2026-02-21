package amp.awec.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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

	public BlockState(World world, Vec3i pos) {
		this.block = world.getBlock(pos.x, pos.y, pos.z);
		this.metadata = world.getBlockMetadata(pos.x, pos.y, pos.z);
		this.tileEntity = world.getTileEntity(pos.x, pos.y, pos.z);
	}

	public BlockChange setNotify(World world, Vec3i pos) {
		BlockState oldBlock = new BlockState(world, pos);
		BlockChange blockChange = new BlockChange(oldBlock, pos);

		int blockId = 0;
		if (block != null) {
			blockId = block.id();
		}

		int setMetadata = metadata;
		if (setMetadata == -1) {
			// Keep original metadata
			setMetadata = oldBlock.metadata;
		}

		world.setBlockAndMetadataWithNotify(pos.x, pos.y, pos.z, blockId, setMetadata);

		if (tileEntity != null) {
			world.setTileEntity(pos.x, pos.y, pos.z, tileEntity);
		}

		return blockChange;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlockState)) {
			return false;
		}

		BlockState other = (BlockState) obj;
		return this.block == other.block && this.metadata == other.metadata;
	}

	@Override
	public String toString() {
		if (block == null) {
			return "air";
		}
		return block.namespaceId().value() + ":" + metadata;
	}

	@Override
	public int hashCode() {
		if (block == null) {
			return Objects.hash(0, metadata);
		}
		return Objects.hash(block.id(), metadata);
	}
}
