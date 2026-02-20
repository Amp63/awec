package amp.awec.command.argtypes;

import amp.awec.util.DirectionHelper;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.helper.Direction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ArgumentTypeDirection implements ArgumentType<Direction> {
	private static final Map<String, Function<Direction, Direction>> DIRECTION_LOOKUP = new HashMap<String, Function<Direction, Direction>>() {{
		put("up", d -> Direction.UP);
		put("down", d -> Direction.DOWN);
		put("north", d -> Direction.NORTH);
		put("south", d -> Direction.SOUTH);
		put("east", d -> Direction.EAST);
		put("west", d -> Direction.WEST);
		put("forward", d -> d);
		put("backward", Direction::getOpposite);
		put("left", d -> d.rotate(-1));
		put("right", d -> d.rotate(1));
	}};

	public static ArgumentTypeDirection direction() {
		return new ArgumentTypeDirection();
	}

	@Override
	public Direction parse(StringReader stringReader) throws CommandSyntaxException {
		return parse(stringReader, null);
	}

	@Override
	public <S> Direction parse(StringReader reader, S source) throws CommandSyntaxException {
		String directionString = reader.readUnquotedString().toLowerCase();
		Function<Direction, Direction> directionFunc = DIRECTION_LOOKUP.get(directionString);
		if (directionFunc == null) {
			LiteralMessage message = new LiteralMessage("Unrecognized direction");
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}

		Direction facingDir = Direction.EAST;
		if (source != null) {
			Player player = ((CommandSource) source).getSender();
			if (player != null) {
				double pitch = (directionString.equals("left") || directionString.equals("right")) ? 0.0f : player.xRot;
				facingDir = DirectionHelper.getMajorDirection(pitch, player.yRot);
			}
		}

		return directionFunc.apply(facingDir);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemainingLowerCase();

		for (String key : DIRECTION_LOOKUP.keySet()) {
			if (key.startsWith(remaining)) {
				builder.suggest(key);
			}
		}

 		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return DIRECTION_LOOKUP.keySet();
	}
}
