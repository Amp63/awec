package amp.awec.command.operation;
import amp.awec.operation.ReplaceOperation;
import amp.awec.pattern.ArgumentTypePattern;
import amp.awec.pattern.BlockPattern;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;

public class CommandReplace implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/replace")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("target_pattern", ArgumentTypePattern.replace())
				.then(ArgumentBuilderRequired.argument("replace_pattern", ArgumentTypePattern.normal())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						Player player = source.getSender();
						if (player == null) {
							return 0;
						}

						BlockPattern targetPattern = context.getArgument("target_pattern", BlockPattern.class);
						BlockPattern replaceWithPattern = context.getArgument("replace_pattern", BlockPattern.class);

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
						int changedBlocks = ReplaceOperation.doReplace(world, playerData.selection, targetPattern, replaceWithPattern);

						source.sendMessage("Changed " + changedBlocks + " blocks");
						return 1;
					})
				)));
	}
}
