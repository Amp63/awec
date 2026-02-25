package amp.awec.command.argtypes;

import amp.awec.pattern.BlockAliases;
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
import net.minecraft.core.net.command.CommandSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ArgumentTypeBlockPattern implements ArgumentType<BlockPattern> {
	private static final String[] EXAMPLES = {"stone", "50%stone,50%dirt", "10%dirt", "10%dirt,stone"};

	private static List<String> currentSuggestions = null;

	public static ArgumentTypeBlockPattern pattern() {
		return new ArgumentTypeBlockPattern();
	}

	private boolean isValidPatternChar(char c) {
		return c >= '0' && c <= '9' ||
			   c >= 'A' && c <= 'Z' ||
			   c >= 'a' && c <= 'z' ||
			   c == '_' || c == ':' || c == '%' || c == ',';
	}

	private String readPatternString(StringReader reader) {
		int start = reader.getCursor();

		while(reader.canRead() && isValidPatternChar(reader.peek())) {
			reader.skip();
		}

		return reader.getString().substring(start, reader.getCursor());
	}

	@Override
	public BlockPattern parse(StringReader stringReader) throws CommandSyntaxException {
		return parse(stringReader, null);
	}

	@Override
	public <S> BlockPattern parse(StringReader reader, S source) throws CommandSyntaxException {
		String patternString = readPatternString(reader);
		try {
			BlockPattern pattern = new BlockPattern(patternString, ((CommandSource) source).getSender());
			currentSuggestions = null;
			return pattern;
		}
		catch (BlockPattern.UnrecognizedBlockException e) {
			currentSuggestions = SuggestionHelper.getBlockSuggestions(e.partialString);
			LiteralMessage message = new LiteralMessage(e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
		catch (BlockPattern.BlockPatternException e) {
			LiteralMessage message = new LiteralMessage(e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemainingLowerCase();
		int lastOperatorIndex = RegexUtil.lastIndexOfSet(remaining, "[,%]");
		String suggestionPrefix = "";
		if (lastOperatorIndex != -1) {
			suggestionPrefix = remaining.substring(0, lastOperatorIndex+1);
		}

		if (currentSuggestions != null) {
			for (String suggestion : currentSuggestions) {
				builder.suggest(suggestionPrefix + suggestion);
			}
		}

		return builder.buildFuture();	}

	@Override
	public Collection<String> getExamples() {
		return Arrays.asList(EXAMPLES);
	}
}
