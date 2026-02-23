package amp.awec.command.argtypes;

import amp.awec.schematic.SchematicsManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ArgumentTypeSchematicPath implements ArgumentType<String> {
	public static ArgumentTypeSchematicPath path() {
		return new ArgumentTypeSchematicPath();
	}

	@Override
	public String parse(StringReader stringReader) throws CommandSyntaxException {
		return stringReader.readString();
	}

	@Override
	public <S> String parse(StringReader reader, S source) throws CommandSyntaxException {
		return parse(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemainingLowerCase();

		try {
			for (String path : SchematicsManager.getAllFilePaths()) {
				if (path.startsWith(remaining)) {
					if (path.contains("/")) {
						builder.suggest('"' + path + '"');
					}
					else {
						builder.suggest(path);
					}
				}
			}
		} catch (IOException ignored) {}

		return builder.buildFuture();
	}
}
