package amp.awec.command.argtypes;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.util.helper.DyeColor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ArgumentTypeDyeColor implements ArgumentType<DyeColor> {
	public static final Map<String, DyeColor> DYE_COLOR_LOOKUP = new HashMap<>();

	static {
		for (DyeColor color : DyeColor.blockOrderedColors()) {
			DYE_COLOR_LOOKUP.put(color.colorID, color);
		}
	}

	public static ArgumentTypeDyeColor color() {
		return new ArgumentTypeDyeColor();
	}

	@Override
	public DyeColor parse(StringReader stringReader) throws CommandSyntaxException {
		String readString = stringReader.readUnquotedString();
		if (!DYE_COLOR_LOOKUP.containsKey(readString)) {
			LiteralMessage message = new LiteralMessage("Unrecognized color");
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}

		return DYE_COLOR_LOOKUP.get(readString);
	}

	@Override
	public <S> DyeColor parse(StringReader reader, S source) throws CommandSyntaxException {
		return parse(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemainingLowerCase();

		for (String color : DYE_COLOR_LOOKUP.keySet()) {
			if (color.startsWith(remaining)) {
				builder.suggest(color);
			}
		}

		return builder.buildFuture();
	}
}
