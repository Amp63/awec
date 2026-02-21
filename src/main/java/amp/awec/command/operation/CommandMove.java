package amp.awec.command.operation;

import amp.awec.command.CommandHelper;
import amp.awec.command.argtypes.ArgumentTypeDirection;
import amp.awec.data.PlayerData;
import amp.awec.operation.MoveOperation;
import amp.awec.operation.WorldChange;
import amp.awec.operation.StackOperation;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.DirectionHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;

public class CommandMove implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/move")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					return handleMoveCommand(context, 1, null);
				})
				.then(ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(1, 255))
					.executes(context -> {
						int amount = context.getArgument("amount", Integer.class);
						return handleMoveCommand(context, amount, null);
					})
					.then(ArgumentBuilderRequired.argument("direction", ArgumentTypeDirection.direction())
						.executes(context -> {
							int amount = context.getArgument("amount", Integer.class);
							Direction direction = context.getArgument("direction", Direction.class);
							return handleMoveCommand(context, amount, direction);
						}))
				));
	}

	private int handleMoveCommand(CommandContext<Object> context, int amount, Direction direction) {
		CommandSource source = (CommandSource) context.getSource();
		PlayerData playerData = CommandHelper.getPlayerData(source);
		if (playerData == null) {
			return 0;
		}

		if (direction == null) {
			// Default to forward direction
			Player player = playerData.parentPlayer;
			direction = DirectionHelper.getMajorDirection(player.xRot, player.yRot);
		}


		World world = source.getWorld();
		WorldChange result = MoveOperation.execute(world, playerData.selection, amount, direction);
		playerData.undoHistory.add(result);

		source.sendMessage("Changed " + result.changedBlockCount + " blocks");

		return 1;
	}
}
