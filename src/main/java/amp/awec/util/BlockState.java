package amp.awec.util;

import amp.awec.WorldEditMod;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockState {
	public @Nullable Block<?> block;
	public int metadata = 0;
	public TileEntity tileEntity = null;

	public BlockState(@Nullable Block<?> block, int metadata) {
		initialize(block, metadata, null);
	}

	public BlockState(World world, Vec3i pos) {
		initialize(
			world.getBlock(pos.x, pos.y, pos.z),
			world.getBlockMetadata(pos.x, pos.y, pos.z),
			world.getTileEntity(pos.x, pos.y, pos.z)
		);
	}

	public BlockState(BlockState other) {
		initialize(other.block, other.metadata, other.tileEntity);
	}

	private void initialize(@Nullable Block<?> block, int metadata, @Nullable TileEntity tileEntity) {
		this.block = block;
		this.metadata = metadata;
		this.tileEntity = tileEntity;
	}

	public BlockState setNotify(World world, Vec3i pos) {
		BlockState oldBlock = new BlockState(world, pos);

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
			try {
				TileEntity tileEntityCopy = tileEntity.getClass().newInstance();
				CompoundTag copiedTag = new CompoundTag();
				tileEntity.writeToNBT(copiedTag);
				tileEntityCopy.readFromNBT(copiedTag);
				this.tileEntity = tileEntityCopy;
				WorldEditMod.LOGGER.info("Created copy of tile entity");
				world.setTileEntity(pos.x, pos.y, pos.z, tileEntityCopy);
			}
			catch (Exception e) {
				WorldEditMod.LOGGER.error("Failed to create copy of tile entity at " + pos);
			}
		}

		return oldBlock;
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
