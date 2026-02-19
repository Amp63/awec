package amp.awec.pattern;

import amp.awec.util.PatternType;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.net.command.CommandSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ArgumentTypePattern implements ArgumentType<BlockPattern> {
	private final PatternType patternType;
	private static final String[] EXAMPLES = {"stone", "50%stone,50%dirt", "10%dirt", "10%dirt,stone"};

	public ArgumentTypePattern(PatternType type) {
		patternType = type;
	}

	public static ArgumentTypePattern normal() {
		return new ArgumentTypePattern(PatternType.NORMAL);
	}

	public static ArgumentTypePattern replace() {
		return new ArgumentTypePattern(PatternType.REPLACE);
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
		String patternString = readPatternString(stringReader);
		try {
			return new BlockPattern(patternString, null, patternType);
		} catch (BlockPatternException e) {
			LiteralMessage message = new LiteralMessage(e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
	}

	@Override
	public <S> BlockPattern parse(StringReader reader, S source) throws CommandSyntaxException {
		String patternString = readPatternString(reader);
		try {
			return new BlockPattern(patternString, ((CommandSource) source).getSender(), patternType);
		} catch (BlockPatternException e) {
			LiteralMessage message = new LiteralMessage(e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return ArgumentType.super.listSuggestions(context, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return Arrays.asList(EXAMPLES);
	}
}
