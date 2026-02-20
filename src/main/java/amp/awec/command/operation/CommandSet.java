package amp.awec.command.operation;
import amp.awec.operation.SetOperation;
import amp.awec.command.argtypes.ArgumentTypePattern;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.pattern.BlockPattern;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;

public class CommandSet implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/set")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("pattern", ArgumentTypePattern.normal())
					.executes(context -> {
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
						int changedBlocks = SetOperation.doSet(world, playerData.selection, pattern);

						source.sendMessage("Changed " + changedBlocks + " blocks");
						return 1;
					})
				));
	}
}
