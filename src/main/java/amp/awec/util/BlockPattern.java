package amp.awec.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
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
		return this.blockState.block.namespaceId().value() + ": " + this.probability;
	}
}

public class BlockPattern {
	private static final Pattern percentagePattern = Pattern.compile("(?:(\\d+)%)?([A-Za-z0-9_]+)(?::(\\d+))?");
	private final ArrayList<BlockProbability> patternBlocks = new ArrayList<>();
	private final Random rng = new Random();

	public BlockPattern(String patternString) throws BlockPatternException {
		double cumulativePercentage = 0.0;
		for (String patternBlock : patternString.split(",")) {
			Matcher matcher = percentagePattern.matcher(patternBlock);
			if (!matcher.find()) {
				throw new BlockPatternException("Got pattern syntax error at \"" + patternBlock + "\"");
			}

			// Parse percentage (optional)
			String percentageString = matcher.group(1);
			double percentage = -1.0;
			if (percentageString != null) {
				percentage = Double.parseDouble(percentageString) / 100.0;
				cumulativePercentage += percentage;
			}

			// Parse block name (required)
			String blockName = matcher.group(2);
			Block<?> block = null;

			// Try to parse as numeric ID first
			try {
				int blockId = Integer.parseInt(blockName);
				if (blockId < Blocks.blocksList.length) {
					block = Blocks.getBlock(blockId);
				}
			}
			catch (NumberFormatException ignored) {}

			if (block == null) {
				// Could not parse as ID; try to find using block namespace
				try {
					NamespaceID blockNamespaceId = NamespaceID.getPermanent("minecraft:block/" + blockName);
					block = Blocks.blockMap.get(blockNamespaceId);
				}
				catch (HardIllegalArgumentException ignored) {}
			}

			if (block == null) {
				// Could not parse block type, exit
				throw new BlockPatternException("Unrecognized block name \"" + blockName + "\"");
			}

			// Parse metadata number (optional)
			String metadataString = matcher.group(3);
			int metadata = 0;
			if (metadataString != null) {
				metadata = Integer.parseInt(metadataString);
			}

			BlockState blockState = new BlockState(block, metadata);
			patternBlocks.add(new BlockProbability(blockState, percentage));

		}

		if (cumulativePercentage > 1.0) {
			throw new BlockPatternException("Pattern percentages sum to a number greater than 100");
		}

		// Divide remaining percentage among blocks that were given no percentage
		long noPercentBlockCount = patternBlocks.stream().filter(b -> b.probability == -1).count();
		double divyPercentage = (1.0 - cumulativePercentage) / noPercentBlockCount;
		for (BlockProbability blockProb : patternBlocks) {
			if (blockProb.probability == -1) {
				blockProb.probability = divyPercentage;
			}
		}

		patternBlocks.sort(Comparator.comparingDouble(b -> b.probability));
	}

	@Nullable
	public BlockState sample() {
		double r = rng.nextDouble();
		double totalProbability = 0.0f;

		for (BlockProbability blockProb : patternBlocks) {
			totalProbability += blockProb.probability;
			if (r < totalProbability) {
				return blockProb.blockState;
			}
		}
		return null;
	}

	public String toString() {
		return "BlockPattern(" + patternBlocks.toString() + ")";
	}
}
