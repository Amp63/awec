package amp.awec.util;

import amp.awec.WorldEditMod;
import amp.awec.pattern.BlockMask;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockState {
	public @NotNull Block<?> block;
	public int metadata = 0;
	public TileEntity tileEntity = null;

	public BlockState(@NotNull Block<?> block, int metadata) {
		initialize(block, metadata, null);
	}

	public BlockState(World world, TilePos pos) {
		initialize(
			world.getBlockType(pos),
			world.getBlockData(pos),
			world.getTileEntity(pos)
		);
	}

	public BlockState(BlockState other) {
		initialize(other.block, other.metadata, other.tileEntity);
	}

	private void initialize(@NotNull Block<?> block, int metadata, @Nullable TileEntity tileEntity) {
		this.block = block;
		this.metadata = metadata;
		this.tileEntity = tileEntity;
	}

	public BlockState set(World world, TilePos pos) {
		return set(world, pos, BlockMask.ANY);
	}

	public @Nullable BlockState set(World world, TilePos pos, BlockMask mask) {
		BlockState oldBlock = new BlockState(world, pos);
		if (!mask.matches(oldBlock)) {
			return null;
		}

		int setMetadata = metadata;
		if (setMetadata == -1) {
			// Keep original metadata
			setMetadata = oldBlock.metadata;
		}

		world.setBlockTypeDataRaw(pos, block, setMetadata);
		world.markBlockNeedsUpdate(pos);

		if (tileEntity != null) {
			try {
				TileEntity tileEntityCopy = tileEntity.getClass().getDeclaredConstructor().newInstance();
				CompoundTag copiedTag = new CompoundTag();
				tileEntity.writeToNBT(copiedTag);
				tileEntityCopy.readFromNBT(copiedTag);
				this.tileEntity = tileEntityCopy;
				WorldEditMod.LOGGER.info("Created copy of tile entity");
				world.setTileEntity(pos, tileEntityCopy);
			}
			catch (Exception e) {
				WorldEditMod.LOGGER.error("Failed to create copy of tile entity at " + pos);
			}
		}

		return oldBlock;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlockState other)) {
			return false;
		}

		return this.block == other.block && this.metadata == other.metadata;
	}

	@Override
	public String toString() {
		return block.namespaceId().value() + ":" + metadata;
	}

	@Override
	public int hashCode() {
		return Objects.hash(block.id(), metadata);
	}
}
