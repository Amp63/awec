package amp.awec.command.operation;
import amp.awec.command.CommandPlayerData;
import amp.awec.operation.WorldChange;
import amp.awec.operation.WallsOperation;
import amp.awec.command.argtypes.ArgumentTypeBlockPattern;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.pattern.BlockPattern;
import amp.awec.util.MessageHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandWalls implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/walls")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("pattern", ArgumentTypeBlockPattern.pattern())
					.executes(context -> {
						return handleWallsCommand(context, 1);
					})
					.then(ArgumentBuilderRequired.argument("thickness", ArgumentTypeInteger.integer(1))
						.executes(context -> {
							int thickness = context.getArgument("thickness", Integer.class);
							handleWallsCommand(context, thickness);
							return 1;
						}))
				)
		);
	}

	private int handleWallsCommand(CommandContext<Object> context, int thickness) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);
		if (playerData == null) {
			return 0;
		}

		BlockPattern pattern = context.getArgument("pattern", BlockPattern.class);

		WorldChange result = WallsOperation.execute(playerData.world, playerData.getSelection(), pattern, thickness);
		playerData.addUndoChange(result);

		MessageHelper.info(source, "Changed " + result.changedBlockCount + " blocks");

		return 1;
	}
}
