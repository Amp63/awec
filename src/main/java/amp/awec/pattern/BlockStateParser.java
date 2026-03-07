package amp.awec.pattern;

import amp.awec.util.BlockState;
import amp.awec.util.Vec3i;
import amp.awec.util.WandHelper;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockStateParser {
	private static final Pattern blockStringPattern = Pattern.compile("([A-Za-z0-9_]+)(?::(\\d+))?");

	private Map<String, BlockState> blockAliases;

	public static class BlockStateException extends Exception {
		public BlockStateException(String message) {
			super(message);
		}
	}

	public static class UnrecognizedBlockException extends BlockStateException {
		public String partialString;
		public UnrecognizedBlockException(String errorMessage, String partialString) {
			super(errorMessage);
			this.partialString = partialString;
		}
	}

	public BlockStateParser(@Nullable Player player) {
		setAliases(player);
	}

	private void setAliases(@Nullable Player player) {
		blockAliases = new HashMap<>(BlockAliases.aliasMap);

		// Set custom entries
		if (player == null) {
			return;
		}

		// Hand
		ItemStack heldItem = player.getHeldItem();
		if (heldItem != null) {
			if (heldItem.itemID < Blocks.highestBlockId) {
				Block<?> handBlock = Blocks.getBlock(heldItem.itemID);
				BlockState handBlockState = new BlockState(handBlock, -1);
				BlockState strictHandBlockState = new BlockState(handBlock, heldItem.getMetadata());
				blockAliases.put("hand", handBlockState);
				blockAliases.put("HAND", strictHandBlockState);
			}
		}

		// Target
		if (player.world != null) {
			Vec3i targetPos = WandHelper.getTargetedPos(player, 1.0f);
			BlockState strictTargetBlock = new BlockState(player.world, targetPos);
			if (strictTargetBlock.block != null) {
				BlockState targetBlock = new BlockState(strictTargetBlock.block, -1);
				blockAliases.put("target", targetBlock);
				blockAliases.put("TARGET", strictTargetBlock);
			}
		}

		// Hotbar
		int hotbarOffset = player.inventory.getHotbarOffset();
		for (int i = 0; i < 9; i++) {
			int itemIndex = i + hotbarOffset * 9;
			ItemStack hotbarItem = player.inventory.mainInventory[itemIndex];
			if (hotbarItem != null) {
				if (hotbarItem.itemID < Blocks.highestBlockId) {
					Block<?>hotbarBlock = Blocks.getBlock(hotbarItem.itemID);
					BlockState hotbarBlockState = new BlockState(hotbarBlock, -1);
					BlockState strictHotbarBlockState = new BlockState(hotbarBlock, hotbarItem.getMetadata());
					blockAliases.put("h" + (i+1), hotbarBlockState);
					blockAliases.put("H" + (i+1), strictHotbarBlockState);
				}
			}
		}
	}

	public BlockState parse(String blockStateString) throws BlockStateException {
		Matcher matcher = blockStringPattern.matcher(blockStateString);
		if (!matcher.find()) {
			throw new BlockStateException("Invalid block state string");
		}

		// Parse metadata number (optional)
		String metadataString = matcher.group(2);
		int metadata = -1;
		if (metadataString != null) {
			metadata = Integer.parseInt(metadataString);
		}

		String blockName = matcher.group(1);
		boolean blockFound = false;
		Block<?> block = null;

		// Try to parse as numeric ID first
		try {
			int blockId = Integer.parseInt(blockName);
			if (blockId < Blocks.highestBlockId) {
				block = Blocks.getBlock(blockId);
				blockFound = true;
			}
		}
		catch (NumberFormatException ignored) {}

		if (!blockFound) {
			// Check in alias map
			if (blockAliases.containsKey(blockName)) {
				BlockState aliasState = blockAliases.get(blockName);
				if (metadata != -1) {
					return new BlockState(aliasState.block, aliasState.metadata + metadata);
				}
				return aliasState;
			}
		}

		if (!blockFound) {
			// Try to find using block namespace
			try {
				NamespaceID blockNamespaceId = NamespaceID.getTemp("minecraft:block/" + blockName);
				if (Blocks.blockMap.containsKey(blockNamespaceId)) {
					block = Blocks.blockMap.get(blockNamespaceId);
					blockFound = true;
				}
			}
			catch (HardIllegalArgumentException ignored) {}
		}

		if (!blockFound) {
			// Could not parse block type, exit
			throw new UnrecognizedBlockException("Unrecognized block name", blockName);
		}

		return new BlockState(block, metadata);
	}
}
