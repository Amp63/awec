package amp.awec.command.operation;
import amp.awec.command.CommandHelper;
import amp.awec.operation.WorldChange;
import amp.awec.operation.WallsOperation;
import amp.awec.command.argtypes.ArgumentTypePattern;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.pattern.BlockPattern;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;

public class CommandWalls implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/walls")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("pattern", ArgumentTypePattern.normal())
					.executes(context -> {
						return handleWallsCommand(context, 1);
					})
					.then(ArgumentBuilderRequired.argument("thickness", ArgumentTypeInteger.integer(1))
						.executes(context -> {
							int thickness = context.getArgument("thickness", Integer.class);
							handleWallsCommand(context, thickness);
							return 1;
						}))
				));
	}

	private int handleWallsCommand(CommandContext<Object> context, int thickness) {
		CommandSource source = (CommandSource) context.getSource();
		PlayerData playerData = CommandHelper.getPlayerData(source);
		if (playerData == null) {
			return 0;
		}

		BlockPattern pattern = context.getArgument("pattern", BlockPattern.class);

		World world = source.getWorld();
		WorldChange result = WallsOperation.execute(world, playerData.selection, pattern, thickness);
		playerData.undoHistory.add(result);

		source.sendMessage("Changed " + result.changedBlockCount + " blocks");

		return 1;
	}
}
