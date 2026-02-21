package amp.awec.command.undoredo;

import amp.awec.command.CommandHelper;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class CommandUndoBase {

	@SuppressWarnings("unchecked")
	public static void register(CommandDispatcher<CommandSource> dispatcher, String command, BiFunction<World, PlayerData, Boolean> undoRedoFunction,
								String successMessage, String nothingMessage) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal(command)
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					return handleUndoRedo(source, 1, undoRedoFunction, successMessage, nothingMessage);
				})
				.then(ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(1, 255))
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						int amount = context.getArgument("amount", Integer.class);
						return handleUndoRedo(source, amount, undoRedoFunction, successMessage, nothingMessage);
					})
				)
		);
	}

	private static int handleUndoRedo(CommandSource source, int amount, BiFunction<World, PlayerData, Boolean> undoRedoFunction,
									  String successMessage, String nothingMessage) {
		PlayerData playerData = CommandHelper.getPlayerData(source);
		if (playerData == null) {
			return 0;
		}

		World world = source.getWorld();

		boolean success = false;
		int i;
		for (i = 0; i < amount; i++) {
			boolean result = undoRedoFunction.apply(world, playerData);
			if (!result) {
				break;
			}
			success = true;
		}

		if (success) {
			source.sendMessage(String.format(successMessage, i+1));
			return 1;
		}

		source.sendMessage(nothingMessage);
		return 1;
	}
}
