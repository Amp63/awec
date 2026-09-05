package amp.awec.command.operation;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeDirection;
import amp.awec.operation.MoveOperation;
import amp.awec.operation.WorldChange;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.DirectionHelper;
import amp.awec.util.MessageHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.helper.Direction;
import org.jetbrains.annotations.Nullable;

public class CommandMoveBase {
	@SuppressWarnings("unchecked")
	public static void register(CommandDispatcher<CommandSource> dispatcher, String command, boolean shiftSelection) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal(command)
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					return handleMoveCommand(context, 1, null, shiftSelection);
				})
				.then(ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(1, 255))
					.executes(context -> {
						int amount = context.getArgument("amount", Integer.class);
						return handleMoveCommand(context, amount, null, shiftSelection);
					})
					.then(ArgumentBuilderRequired.argument("direction", ArgumentTypeDirection.direction())
						.executes(context -> {
							int amount = context.getArgument("amount", Integer.class);
							Direction direction = context.getArgument("direction", Direction.class);
							return handleMoveCommand(context, amount, direction, shiftSelection);
						}))
				));
	}

	private static int handleMoveCommand(CommandContext<Object> context, int amount, @Nullable Direction direction, boolean shiftSelection) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);

		if (playerData == null) {
			return 0;
		}

		if (direction == null) {
			// Default to forward direction
			direction = DirectionHelper.getMajorDirection(playerData.player.xRot, playerData.player.yRot);
		}

		WorldChange result = MoveOperation.execute(playerData.world, playerData.getSelection(), amount, direction, shiftSelection);
		playerData.addUndoChange(result);

		MessageHelper.info(source, "Changed " + result.changedBlockCount + " blocks");

		return 1;
	}
}
