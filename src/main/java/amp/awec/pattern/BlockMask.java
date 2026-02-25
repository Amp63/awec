package amp.awec.pattern;

import amp.awec.WorldEditMod;
import amp.awec.util.BlockState;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

abstract class MaskElement {
	abstract public boolean matches(BlockState blockState);
}

class RandomBlock extends MaskElement {
	private final BlockState blockState;
	private final double probability;
	private final Random rng = new Random();

	public RandomBlock(BlockState blockState, double probability) {
		this.blockState = blockState;
		this.probability = probability;
	}

	@Override
	public boolean matches(BlockState other) {
		boolean metaMatch = blockState.metadata == -1 | blockState.metadata == other.metadata;
		return blockState.block == other.block &&
			metaMatch &&
			rng.nextDouble() < probability;
	}
}

class BlockTag extends MaskElement {
	private final Tag<Block<?>> tag;

	public BlockTag(Tag<Block<?>> tag) {
		this.tag = tag;
	}

	@Override
	public boolean matches(BlockState blockState) {
		if (blockState.block == null) {
			return false;
		}
		return blockState.block.isIn(this.tag);
	}
}

public class BlockMask {
	private static final String splitPattern = "(?=[|&!])|(?<=[|&!])";
	private static final Pattern operatorPattern = Pattern.compile("[|&!]");
	private static final String binaryOperatorPattern  = "[|&]";
	private static final Pattern randomBlockPattern = Pattern.compile("(?:(\\d+)%)?([A-Za-z0-9_]+(?::\\d+)?)");
	private static final Pattern blockTagPattern = Pattern.compile("#([a-zA-Z_]+)");

	private final BlockStateParser blockStateParser;
	private final List<Object> rpnTokens = new ArrayList<>();

	public static class BlockMaskException extends Exception {
		public BlockMaskException(String errorMessage) {
			super(errorMessage);
		}
	}

	public static class RandomBlockException extends BlockMaskException {
		public String partialString;
		public RandomBlockException(String errorMessage, String partialString) {
			super(errorMessage);
			this.partialString = partialString;
		}
	}

	public static class BlockTagException extends BlockMaskException {
		public String partialString;
		public BlockTagException(String errorMessage, String partialString) {
			super(errorMessage);
			this.partialString = partialString;
		}
	}

	public BlockMask(String maskString) throws BlockMaskException {
		blockStateParser = new BlockStateParser(null);
		parseMask(maskString);
	}

	public BlockMask(String maskString, @Nullable Player player) throws BlockMaskException {
		blockStateParser = new BlockStateParser(player);
		parseMask(maskString);
	}

	public boolean matches(BlockState blockState) {
		Deque<Boolean> stack = new ArrayDeque<>();

		for (Object token : rpnTokens) {
			if (token instanceof MaskElement) {
				stack.push(((MaskElement) token).matches(blockState));
			}
			else {
				String op = (String) token;
				switch (op) {
					case "!": {
						boolean operand = stack.pop();
						stack.push(!operand);
						break;
					}
					case "&": {
						boolean right = stack.pop();
						boolean left  = stack.pop();
						stack.push(left && right);
						break;
					}
					case "|": {
						boolean right = stack.pop();
						boolean left  = stack.pop();
						stack.push(left || right);
						break;
					}
				}
			}
		}

		return stack.pop();
	}

	private void parseMask(String maskString) throws BlockMaskException {
		Deque<String> operatorStack = new ArrayDeque<>();
		int elementCount = 0;
		int binaryOperatorCount = 0;

		for (String token : maskString.split(splitPattern)) {
			if (token.isEmpty()) continue;

			Matcher operatorMatcher = operatorPattern.matcher(token);
			if (operatorMatcher.find()) {
				String op = token;
				while (!operatorStack.isEmpty() &&
					precedence(operatorStack.peek()) > precedence(op)) {
					rpnTokens.add(operatorStack.pop());
				}
				operatorStack.push(op);
				if (op.matches(binaryOperatorPattern)) {
					binaryOperatorCount++;
				}
				continue;
			}

			Matcher randomBlockMatcher = randomBlockPattern.matcher(token);
			Matcher blockTagMatcher    = blockTagPattern.matcher(token);
			MaskElement parsedElement;

			if (blockTagMatcher.find()) {
				parsedElement = parseBlockTag(blockTagMatcher);
			} else if (randomBlockMatcher.find()) {
				parsedElement = parseRandomBlock(randomBlockMatcher);
			} else {
				throw new BlockMaskException("Unrecognized mask element type");
			}

			rpnTokens.add(parsedElement);
			elementCount++;
		}

		while (!operatorStack.isEmpty()) {
			rpnTokens.add(operatorStack.pop());
		}

		if (binaryOperatorCount >= elementCount) {
			throw new BlockMaskException("Got dangling operator in mask expression");
		}
	}

	private static int precedence(String op) {
		switch (op) {
			case "!": return 3;
			case "&": return 2;
			case "|": return 1;
			default:  return 0;
		}
	}

	private RandomBlock parseRandomBlock(Matcher matcher) throws BlockMaskException {
		// Parse percentage (optional)
		String percentageString = matcher.group(1);
		double percentage = 1.0;
		if (percentageString != null) {
			percentage = Double.parseDouble(percentageString) / 100.0;
		}

		try {
			BlockState blockState = blockStateParser.parse(matcher.group(2));
			return new RandomBlock(blockState, percentage);
		}
		catch (BlockStateParser.UnrecognizedBlockException e) {
			throw new RandomBlockException(e.getMessage(), matcher.group(2));
		}
		catch (BlockStateParser.BlockStateException e) {
			throw new BlockMaskException(e.getMessage());
		}
	}

	private BlockTag parseBlockTag(Matcher matcher) throws BlockTagException {
		String tagString = matcher.group(1);
		Tag<Block<?>> tag = BlockTags.TAG_LIST.stream()
			.filter(t -> t.getName().equals(tagString))
			.findFirst()
			.orElse(null);

		if (tag == null) {
			throw new BlockTagException("Unrecognized block tag", tagString);
		}

		return new BlockTag(tag);
	}
}
