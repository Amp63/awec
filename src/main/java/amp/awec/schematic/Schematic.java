package amp.awec.schematic;

import amp.awec.util.BlockState;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolumeBuffer;
import com.mojang.nbt.tags.*;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Format changes:
 *     - Palette indices are stored using IntTag instead of varint
 *     - Block Entity `Id` field is omitted
 *     		- This is because tile entities don't have a dedicated namespaceID registry, so we just use
 *     		  the block ID at that location to get which tile entity class should be used.
 */
public class Schematic {
	private static final int SCHEMATIC_VERSION = 3;
	private static final int DATA_VERSION = 22010001;

	private final CompoundTag data;

	public Schematic() {
		CompoundTag schematicDataTag = new CompoundTag();
		schematicDataTag.putInt("Version", SCHEMATIC_VERSION);
		schematicDataTag.putInt("DataVersion", DATA_VERSION);
		schematicDataTag.putCompound("Metadata", createMetadataTag("", "", 0, null));

		schematicDataTag.putShort("Width", (short) 0);
		schematicDataTag.putShort("Height", (short) 0);
		schematicDataTag.putShort("Length", (short) 0);

		schematicDataTag.putList("Offset", vec3iToListTag(new Vec3i(0, 0, 0)));

		schematicDataTag.putCompound("Blocks", new CompoundTag());

		CompoundTag schematicTag = new CompoundTag();
		schematicTag.putCompound("Schematic", schematicDataTag);
		CompoundTag rootTag = new CompoundTag();
		rootTag.putCompound("", schematicTag);

		data = rootTag;
	}

	public Schematic(String filePath) throws IOException {
		data = new CompoundTag();
		this.readFromFile(filePath);
	}

	public static Schematic fromVolumeBuffer(CuboidVolumeBuffer buffer, Vec3i offset) {
		Schematic schem = new Schematic();
		CompoundTag schematicTag = schem.data.getCompound("").getCompound("Schematic");

		Vec3i bufferDim = buffer.getDim();

		schematicTag.putShort("Width", (short) bufferDim.x);
		schematicTag.putShort("Height", (short) bufferDim.y);
		schematicTag.putShort("Length", (short) bufferDim.z);
		schematicTag.putList("Offset", vec3iToListTag(offset));

		CompoundTag paletteTag = new CompoundTag();
		int paletteIndex = 0;

		IntTag[] blockData = new IntTag[bufferDim.x * bufferDim.y * bufferDim.z];
		int blockIndex = 0;

		ListTag blockEntitiesTag = new ListTag();

		for (BlockState blockState : buffer.getBlockBuffer()) {
			String namespaceString = blockState.block == null ?
				"minecraft:block/air;0" :
				blockState.block.namespaceId().toString() + ";" + blockState.metadata;

			if (!paletteTag.containsKey(namespaceString)) {
				paletteTag.putInt(namespaceString, paletteIndex);
				paletteIndex++;
			}

			blockData[blockIndex] = new IntTag(paletteTag.getInteger(namespaceString));

			if (blockState.tileEntity != null) {
				Vec3i tileEntityPos = new Vec3i(
					blockIndex % bufferDim.x,
					blockIndex / (bufferDim.x * bufferDim.z),
					blockIndex / bufferDim.x % bufferDim.z
				);
				CompoundTag blockEntityTag = Schematic.createBlockEntityTag(blockState.tileEntity, tileEntityPos);
				blockEntitiesTag.addTag(blockEntityTag);
			}

			blockIndex++;
		}

		// Assemble the schematic
		CompoundTag blocksTag = schematicTag.getCompound("Blocks");

		blocksTag.putCompound("Palette", paletteTag);

		ListTag blockDataTag = new ListTag(Arrays.asList(blockData));
		blocksTag.putList("Data", blockDataTag);

		blocksTag.putList("BlockEntities", blockEntitiesTag);

		return schem;
	}

	public static class LoadResult {
		public CuboidVolumeBuffer buffer;
		public Vec3i offset;
		public LoadResult(CuboidVolumeBuffer buffer, Vec3i offset) {
			this.buffer = buffer; this.offset = offset;
		}
	}
	public LoadResult toVolumeBuffer() throws HardIllegalArgumentException {
		CompoundTag schematicTag = data.getCompound("").getCompound("Schematic");
		int dimX = schematicTag.getShort("Width") & 0xFFFF;
		int dimY = schematicTag.getShort("Height") & 0xFFFF;
		int dimZ = schematicTag.getShort("Length") & 0xFFFF;

		CuboidVolumeBuffer volumeBuffer = new CuboidVolumeBuffer(dimX, dimY, dimZ);
		BlockState[] blockBuffer = volumeBuffer.getBlockBuffer();

		CompoundTag blocksTag = schematicTag.getCompound("Blocks");

		CompoundTag paletteTag = blocksTag.getCompound("Palette");

		// Reverse palette map and convert namespaces into BlockStates
		Map<Integer, BlockState> palette = new HashMap<>();
		for (Map.Entry<String, Tag<?>> entry : paletteTag.getValue().entrySet()) {
			int paletteId = ((IntTag) entry.getValue()).getValue();
			String blockString = entry.getKey();
			String[] blockStringSplit = blockString.split(";");

			NamespaceID blockNamespaceId = NamespaceID.getTemp(blockStringSplit[0]);
			Block<?> block = Blocks.blockMap.get(blockNamespaceId);

			int blockMetadata = Integer.parseInt(blockStringSplit[1]);

			BlockState blockState = new BlockState(block, blockMetadata);
			palette.put(paletteId, blockState);
		}

		ListTag blockDataTag = blocksTag.getList("Data");
		ListTag blockEntitiesTag = blocksTag.getList("BlockEntities");

		// Create block states for the whole volume
		int blockIndex = 0;
		for (Tag<?> paletteIdTag : blockDataTag) {
			int paletteId = ((IntTag) paletteIdTag).getValue();
			BlockState blockState = palette.get(paletteId);

			blockBuffer[blockIndex] = new BlockState(blockState);
			blockIndex++;
		}

		// Create tile entities
		for (Tag<?> blockEntityTag : blockEntitiesTag) {
			ListTag posTag = ((CompoundTag) blockEntityTag).getList("Pos");
			CompoundTag blockEntityData = ((CompoundTag) blockEntityTag).getCompound("Data");

			Vec3i pos = listTagToVec3i(posTag);
			int blockStateIndex = pos.x + pos.z * dimX + pos.y * dimX * dimZ;
			BlockState blockState = blockBuffer[blockStateIndex];
			if (blockState.block != null && blockState.block.entitySupplier != null) {
				TileEntity newTileEntity = blockState.block.entitySupplier.get();
				newTileEntity.readFromNBT(blockEntityData);
				blockState.tileEntity = newTileEntity;
			}
		}

		ListTag offsetTag = schematicTag.getList("Offset");
		Vec3i offsetVec = listTagToVec3i(offsetTag);

		return new LoadResult(volumeBuffer, offsetVec);
	}

	public void writeToFile(String filePath) throws IOException {
		DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(Paths.get(filePath))));
		data.write(dos);
		dos.close();
	}

	public void readFromFile(String filePath) throws IOException {
		DataInputStream dis = new DataInputStream(new GZIPInputStream(Files.newInputStream(Paths.get(filePath))));
		data.read(dis);
		dis.close();
	}

	private static CompoundTag createMetadataTag(String name, String author, long date, @NotNull String @Nullable [] requiredMods) {
		CompoundTag tag = new CompoundTag();
		tag.putString("Name", name);
		tag.putString("Author", author);
		tag.putLong("Date", date);

		ListTag requiredModsTag = new ListTag();
		if (requiredMods != null) {
			for (String modId : requiredMods) {
				requiredModsTag.addTag(new StringTag(modId));
			}
		}
		tag.putList("RequiredMods", requiredModsTag);

		return tag;
	}

	private static CompoundTag createBlockEntityTag(TileEntity tileEntity, Vec3i pos) {
		CompoundTag blockEntityTag = new CompoundTag();

		blockEntityTag.putList("Pos", Schematic.vec3iToListTag(pos));

		CompoundTag blockEntityData = new CompoundTag();
		tileEntity.writeToNBT(blockEntityData);
		blockEntityTag.putCompound("Data", blockEntityData);

		return blockEntityTag;
	}

	private static ListTag vec3iToListTag(Vec3i v) {
		return new ListTag(Arrays.asList(new IntTag[] {
			new IntTag(v.x), new IntTag(v.y), new IntTag(v.z)
		}));
	}

	private static Vec3i listTagToVec3i(ListTag tag) {
		int x = ((IntTag) tag.tagAt(0)).getValue();
		int y = ((IntTag) tag.tagAt(1)).getValue();
		int z = ((IntTag) tag.tagAt(2)).getValue();
		return new Vec3i(x, y, z);
	}
}
