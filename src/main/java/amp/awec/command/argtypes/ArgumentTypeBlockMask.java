package amp.awec.command.argtypes;

import amp.awec.WorldEditMod;
import amp.awec.pattern.BlockMask;
import amp.awec.pattern.BlockPattern;
import amp.awec.util.RegexUtil;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.net.command.CommandSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ArgumentTypeBlockMask implements ArgumentType<BlockMask> {
	private static final String[] EXAMPLES = {"stone", "50%stone|dirt", "#mineable_by_pickaxe"};

	private static List<String> currentSuggestions = null;

	public static ArgumentTypeBlockMask mask() {
		return new ArgumentTypeBlockMask();
	}

	private boolean isValidMaskChar(char c) {
		return String.valueOf(c).matches("[A-Za-z0-9_:%#&|!]");
	}

	private String readMaskString(StringReader reader) {
		int start = reader.getCursor();

		while(reader.canRead() && isValidMaskChar(reader.peek())) {
			reader.skip();
		}

		return reader.getString().substring(start, reader.getCursor());
	}

	@Override
	public BlockMask parse(StringReader stringReader) throws CommandSyntaxException {
		return parse(stringReader, null);
	}

	@Override
	public <S> BlockMask parse(StringReader reader, S source) throws CommandSyntaxException {
		String maskString = readMaskString(reader);
		try {
			BlockMask mask = new BlockMask(maskString, ((CommandSource) source).getSender());
			currentSuggestions = null;  // Clear suggestions
			return mask;
		}
		catch (BlockMask.RandomBlockException e) {
			currentSuggestions = SuggestionHelper.getBlockSuggestions(e.partialString);
			LiteralMessage message = new LiteralMessage(e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
		catch (BlockMask.BlockTagException e) {
			currentSuggestions = SuggestionHelper.getBlockTagSuggestions(e.partialString);
			LiteralMessage message = new LiteralMessage(e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
		catch (BlockMask.BlockMaskException e) {
			LiteralMessage message = new LiteralMessage(e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemainingLowerCase();
		int lastOperatorIndex = RegexUtil.lastIndexOfSet(remaining, "[|&!%]");
		String suggestionPrefix = "";
		if (lastOperatorIndex != -1) {
			suggestionPrefix = remaining.substring(0, lastOperatorIndex+1);
		}
		if (currentSuggestions != null) {
			for (String suggestion : currentSuggestions) {
				builder.suggest(suggestionPrefix + suggestion);
			}
		}

		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return Arrays.asList(EXAMPLES);
	}
}
