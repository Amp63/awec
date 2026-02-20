package amp.awec.command.operation;
import amp.awec.command.argtypes.ArgumentTypeDirection;
import amp.awec.operation.StackOperation;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
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
		Player player = source.getSender();
		if (player == null) {
			return 0;
		}

		if (direction == null) {
			// Default to forward direction
			direction = DirectionHelper.getMajorDirection(player.xRot, player.yRot);
		}

		PlayerData playerData = WorldEditMod.getPlayerData(player);
		if (playerData == null) {
			source.sendMessage("Failed to access WorldEdit player data");
			return 0;
		}
		if (!playerData.selection.isComplete()) {
			source.sendMessage("Both corners must be set");
			return 0;
		}

		World world = source.getWorld();

		int changedBlocks = StackOperation.doStack(world, playerData.selection, amount, direction);

		source.sendMessage("Changed " + changedBlocks + " blocks");

		return 1;
	}
}
