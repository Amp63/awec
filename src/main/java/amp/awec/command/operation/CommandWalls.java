package amp.awec.command.operation;
import amp.awec.operation.WallsOperation;
import amp.awec.pattern.ArgumentTypePattern;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.pattern.BlockPattern;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.entity.player.Player;
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
		Player player = source.getSender();
		if (player == null) {
			return 0;
		}

		BlockPattern pattern = context.getArgument("pattern", BlockPattern.class);
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
		int changedBlocks = WallsOperation.doWalls(world, playerData.selection, pattern, thickness);

		source.sendMessage("Changed " + changedBlocks + " blocks");

		return 1;
	}
}
