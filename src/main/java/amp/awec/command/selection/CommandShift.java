package amp.awec.command.selection;
import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeDirection;
import amp.awec.operation.WorldChange;
import amp.awec.operation.StackOperation;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.DirectionHelper;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolume;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.helper.Direction;

public class CommandShift implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/shift")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					return handleShiftCommand(context, 1, null);
				})
				.then(ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(1, 255))
					.executes(context -> {
						int amount = context.getArgument("amount", Integer.class);
						return handleShiftCommand(context, amount, null);
					})
					.then(ArgumentBuilderRequired.argument("direction", ArgumentTypeDirection.direction())
						.executes(context -> {
							int amount = context.getArgument("amount", Integer.class);
							Direction direction = context.getArgument("direction", Direction.class);
							return handleShiftCommand(context, amount, direction);
						}))
				));
	}

	private int handleShiftCommand(CommandContext<Object> context, int amount, Direction direction) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);
		if (playerData == null) {
			return 0;
		}

		if (direction == null) {
			// Default to forward direction
			direction = DirectionHelper.getMajorDirection(playerData.player.xRot, playerData.player.yRot);
		}

		Vec3i shiftVector = new Vec3i(direction);
		shiftVector.scalei(amount);
		playerData.getSelection().shift(shiftVector);

		source.sendMessage("Moved selection " + amount + " blocks");

		return 1;
	}
}
