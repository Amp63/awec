package amp.awec.command.operation;
import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeDirection;
import amp.awec.operation.WorldChange;
import amp.awec.operation.StackOperation;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.DirectionHelper;
import amp.awec.util.MessageHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.helper.Direction;

public class CommandStack implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/stack")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					return handleStackCommand(context, 1, null);
				})
				.then(ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(1, 255))
					.executes(context -> {
						int amount = context.getArgument("amount", Integer.class);
						return handleStackCommand(context, amount, null);
					})
					.then(ArgumentBuilderRequired.argument("direction", ArgumentTypeDirection.direction())
						.executes(context -> {
							int amount = context.getArgument("amount", Integer.class);
							Direction direction = context.getArgument("direction", Direction.class);
							return handleStackCommand(context, amount, direction);
						}))
				));
	}

	private int handleStackCommand(CommandContext<Object> context, int amount, Direction direction) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);
		if (playerData == null) {
			return 0;
		}

		if (direction == null) {
			// Default to forward direction
			direction = DirectionHelper.getMajorDirection(playerData.player.xRot, playerData.player.yRot);
		}

		WorldChange result = StackOperation.execute(playerData.world, playerData.getSelection(), amount, direction);
		playerData.addUndoChange(result);

		MessageHelper.info(source, "Changed " + result.changedBlockCount + " blocks");

		return 1;
	}
}
