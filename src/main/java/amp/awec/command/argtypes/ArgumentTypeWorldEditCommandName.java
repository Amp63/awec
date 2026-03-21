package amp.awec.command.argtypes;

import amp.awec.util.HelpList;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class ArgumentTypeWorldEditCommandName implements ArgumentType<String> {
	public static ArgumentTypeWorldEditCommandName command() {
		return new ArgumentTypeWorldEditCommandName();
	}

	@Override
	public String parse(StringReader stringReader) throws CommandSyntaxException {
		String readString = stringReader.readUnquotedString();
		if (!HelpList.commandMap.containsKey(readString)) {
			LiteralMessage message = new LiteralMessage("Unrecognized color");
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}

		return readString;
	}

	@Override
	public <S> String parse(StringReader reader, S source) throws CommandSyntaxException {
		return parse(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemainingLowerCase();

		for (String commandName : HelpList.commandMap.keySet()) {
			if (commandName.startsWith(remaining)) {
				builder.suggest(commandName);
			}
		}

		return builder.buildFuture();
	}
}
