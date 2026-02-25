package amp.awec.pattern;

import amp.awec.util.BlockState;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
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
	private static final Pattern blockProbabilityPattern = Pattern.compile("(?:(\\d+)%)?([A-Za-z0-9_]+(?::\\d+)?)");
	private BlockStateParser blockStateParser;

	private final ArrayList<BlockProbability> blockProbabilities = new ArrayList<>();
	private final Random rng = new Random();

	public static class BlockPatternException extends Exception {
		public BlockPatternException(String errorMessage) {
			super(errorMessage);
		}
	}

	public static class UnrecognizedBlockException extends BlockPatternException {
		public String partialString;
		public UnrecognizedBlockException(String errorMessage, String partialString) {
			super(errorMessage);
			this.partialString = partialString;
		}
	}

	public BlockPattern(@Nullable Block<?> singleBlock) {
		blockProbabilities.add(new BlockProbability(
			new BlockState(singleBlock, 0), 1.0
		));
	}

	public BlockPattern(String patternString) throws BlockPatternException {
		blockStateParser = new BlockStateParser(null);
		parsePattern(patternString);
	}

	public BlockPattern(String patternString, @Nullable Player player) throws BlockPatternException {
		blockStateParser = new BlockStateParser(player);
		parsePattern(patternString);
	}

	private void parsePattern(String patternString) throws BlockPatternException {
		for (String patternBlock : patternString.split(",")) {
			Matcher matcher = blockProbabilityPattern.matcher(patternBlock);
			if (!matcher.find()) {
				throw new BlockPatternException("Got pattern syntax error at \"" + patternBlock + "\"");
			}

			// Parse percentage (optional)
			String percentageString = matcher.group(1);
			double percentage = -1.0;
			if (percentageString != null) {
				percentage = Double.parseDouble(percentageString) / 100.0;
			}

			try {
				BlockState blockState = blockStateParser.parse(matcher.group(2));
				blockProbabilities.add(new BlockProbability(blockState, percentage));
			}
			catch (BlockStateParser.UnrecognizedBlockException e) {
				throw new UnrecognizedBlockException(e.getMessage(), matcher.group(2));
			}
			catch (BlockStateParser.BlockStateException e) {
				throw new BlockPatternException(e.getMessage());
			}
		}

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

	public String toString() {
		return "BlockPattern(" + blockProbabilities.toString() + ")";
	}
}
