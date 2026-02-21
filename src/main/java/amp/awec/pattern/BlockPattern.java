package amp.awec.pattern;

import amp.awec.WorldEditMod;
import amp.awec.util.BlockState;
import amp.awec.util.PatternType;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BlockProbability {
	public BlockState blockState;
	public double probability;

	public BlockProbability(BlockState blockState, double probability) {
		this.blockState = blockState;
		this.probability = probability;
	}

	public String toString() {
		if (this.blockState.block == null) {
			return "air";
		}
		return this.blockState + ": " + this.probability;
	}
}

public class BlockPattern {
	private static final Pattern percentagePattern = Pattern.compile("(?:(\\d+)%)?([A-Za-z0-9_]+)(?::(\\d+))?");
	private final Map<String, Block<?>> blockNameOverrides = new HashMap<>();

	private final ArrayList<BlockProbability> blockProbabilities = new ArrayList<>();
	private final Random rng = new Random();

	public BlockPattern(@Nullable Block<?> singleBlock) {
		blockProbabilities.add(new BlockProbability(
			new BlockState(singleBlock, 0), 1.0
		));
	}

	public BlockPattern(String patternString) throws BlockPatternException {
		setBlockNameOverrides(null);
		parsePattern(patternString, PatternType.NORMAL);
	}

	public BlockPattern(String patternString, @Nullable Player parentPlayer) throws BlockPatternException {
		setBlockNameOverrides(parentPlayer);
		parsePattern(patternString, PatternType.NORMAL);
	}

	public BlockPattern(String patternString, @Nullable Player parentPlayer, PatternType type) throws BlockPatternException {
		setBlockNameOverrides(parentPlayer);
		parsePattern(patternString, type);
	}

	private void setBlockNameOverrides(@Nullable Player parentPlayer) {
		blockNameOverrides.put("air", null);
		blockNameOverrides.put("water", Blocks.FLUID_WATER_STILL);
		blockNameOverrides.put("lava", Blocks.FLUID_LAVA_STILL);

		// Set "hand" entry
		if (parentPlayer == null) {
			return;
		}
		ItemStack heldItem = parentPlayer.getHeldItem();
		if (heldItem == null) {
			return;
		}
		Block<?> handBlock = null;
		if (heldItem.itemID < Blocks.highestBlockId) {
			handBlock = Blocks.getBlock(heldItem.itemID);
		}
		blockNameOverrides.put("hand", handBlock);
	}

	private void parsePattern(String patternString, PatternType type) throws BlockPatternException {
		switch (type) {
			case NORMAL:
				parseNormalPattern(patternString);
				break;
			case REPLACE:
				parseReplacePattern(patternString);
				break;
		}
	}

	private void parseNormalPattern(String patternString) throws BlockPatternException {
		parsePatternString(patternString, -1.0, -1);

		double cumulativeProbability = blockProbabilities.stream()
			.map(b -> b.probability)
			.filter(p -> p != -1)
			.reduce(0.0, Double::sum);

		if (cumulativeProbability > 1.0) {
			throw new BlockPatternException("Pattern percentages sum to a number greater than 100");
		}

		// Divide remaining percentage among blocks that were given no percentage
		long noPercentBlockCount = blockProbabilities.stream().filter(b -> b.probability == -1).count();
		double divyPercentage = (1.0 - cumulativeProbability) / noPercentBlockCount;
		for (BlockProbability blockProb : blockProbabilities) {
			if (blockProb.probability == -1) {
				blockProb.probability = divyPercentage;
			}
		}

		// Sort list ascending by probability
		blockProbabilities.sort(Comparator.comparingDouble(b -> b.probability));
	}

	private void parseReplacePattern(String patternString) throws BlockPatternException {
		parsePatternString(patternString, 1.0, -1);
	}

	private void parsePatternString(String patternString, double defaultProbability, int defaultMetadata) throws BlockPatternException {
		for (String patternBlock : patternString.split(",")) {
			Matcher matcher = percentagePattern.matcher(patternBlock);
			if (!matcher.find()) {
				throw new BlockPatternException("Got pattern syntax error at \"" + patternBlock + "\"");
			}

			// Parse percentage (optional)
			String percentageString = matcher.group(1);
			double percentage = defaultProbability;
			if (percentageString != null) {
				percentage = Double.parseDouble(percentageString) / 100.0;
			}

			// Parse block name (required)
			String blockName = matcher.group(2);
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
				// Could not parse as ID; try to find using block namespace
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
				// Could not find in namespace, check in overrides map
				if (blockNameOverrides.containsKey(blockName)) {
					block = blockNameOverrides.get(blockName);
					blockFound = true;
				}
			}

			if (!blockFound) {
				// Could not parse block type, exit
				throw new BlockPatternException("Unrecognized block name \"" + blockName + "\"");
			}

			// Parse metadata number (optional)
			String metadataString = matcher.group(3);
			int metadata = defaultMetadata;
			if (metadataString != null) {
				metadata = Integer.parseInt(metadataString);
			}

			BlockState blockState = new BlockState(block, metadata);
			blockProbabilities.add(new BlockProbability(blockState, percentage));
		}
	}

	@Nullable
	public BlockState sample() {
		double r = rng.nextDouble();
		double totalProbability = 0.0f;

		for (BlockProbability blockProb : blockProbabilities) {
			totalProbability += blockProb.probability;
			if (r < totalProbability) {
				return blockProb.blockState;
			}
		}
		return null;
	}

	public boolean shouldReplace(BlockState block) {
		for (BlockProbability blockProb : blockProbabilities) {
			if (blockProb.blockState.block != block.block) {
				continue;
			}
			if (blockProb.blockState.metadata == -1 || blockProb.blockState.metadata == block.metadata) {
				return rng.nextDouble() < blockProb.probability;
			}
		}
		return false;  // Block is not in pattern
	}

	public String toString() {
		return "BlockPattern(" + blockProbabilities.toString() + ")";
	}
}
