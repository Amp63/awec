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
import java.util.stream.Stream;

class BlockProbability {
	public Block<?> block;
	public double probability;
	public BlockProbability(Block<?> block, double probability) {
		this.block = block;
		this.probability = probability;
	}

	public String toString() {
		return this.block.namespaceId().value() + ": " + this.probability;
	}
}

public class BlockPattern {
	private static final Pattern percentagePattern = Pattern.compile("(?:(\\d+)%)?([A-Za-z_]+)");
	private final ArrayList<BlockProbability> patternBlocks = new ArrayList<>();
	private final Random rng = new Random();

	public BlockPattern(String patternString) throws BlockPatternException {
		double cumulativePercentage = 0.0;
		for (String patternBlock : patternString.split(",")) {
			Matcher matcher = percentagePattern.matcher(patternBlock);
			if (!matcher.find()) {
				throw new BlockPatternException("Got pattern syntax error at \"" + patternBlock + "\"");
			}

			String percentageString = matcher.group(1);
			double percentage = -1.0;
			if (percentageString != null) {
				percentage = Double.parseDouble(percentageString) / 100.0;
				cumulativePercentage += percentage;
			}

			String blockName = matcher.group(2);
			try {
				NamespaceID blockNamespaceId = NamespaceID.getPermanent("minecraft:block/" + blockName);
				Block<?> block = Blocks.blockMap.get(blockNamespaceId);
				if (block == null) {
					throw new BlockPatternException("Unrecognized block name \"" + blockName + "\"");
				}
				patternBlocks.add(new BlockProbability(block, percentage));
			} catch (HardIllegalArgumentException e) {
				throw new BlockPatternException("Unrecognized block name \"" + blockName + "\"");
			}
		}

		if (cumulativePercentage > 1.0) {
			throw new BlockPatternException("Pattern percentages sum to a number greater than 100");
		}

		long noPercentBlockCount = patternBlocks.stream().filter(b -> b.probability == -1).count();
		double divyPercentage = (1.0 - cumulativePercentage) / noPercentBlockCount;
		for (BlockProbability block : patternBlocks) {
			if (block.probability == -1) {
				block.probability = divyPercentage;
			}
		}

		patternBlocks.sort(Comparator.comparingDouble(b -> b.probability));
	}

	@Nullable
	public Block<?> sample() {
		double r = rng.nextDouble();
		double totalProbability = 0.0f;

		for (BlockProbability block : patternBlocks) {
			totalProbability += block.probability;
			if (r < totalProbability) {
				return block.block;
			}
		}
		return null;
	}

	public String toString() {
		return "BlockPattern(" + patternBlocks.toString() + ")";
	}
}
