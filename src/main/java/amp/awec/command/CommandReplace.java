package amp.awec.command;
import amp.awec.pattern.ArgumentTypePattern;
import amp.awec.pattern.BlockPattern;
import amp.awec.util.*;
import amp.awec.volume.BlockVolumeIterator;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permissions.WorldEditPermissions;
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
						if (!playerData.hasBothCorners()) {
							source.sendMessage("Both corners must be set");
							return 0;
						}

						World world = source.getWorld();
						int changedBlocks = doReplace(world, playerData.corner1, playerData.corner2, targetPattern, replaceWithPattern);

						source.sendMessage("Changed " + changedBlocks + " blocks");
						return 1;
					})
				)));
	}

	private int doReplace(World world, BlockPos corner1, BlockPos corner2, BlockPattern targetPattern, BlockPattern replaceWithPattern) {
		BlockVolumeIterator iterator = new BlockVolumeIterator(corner1, corner2);
		int changedBlocks = 0;

		while (iterator.hasNext()) {
			BlockPos setPos = iterator.next();
			BlockState replacedBlock = new BlockState(world, setPos);
			if (targetPattern.shouldReplace(replacedBlock)) {
				BlockState sampledBlock = replaceWithPattern.sample();
				if (sampledBlock != null) {
					sampledBlock.setNotify(world, setPos);
					changedBlocks++;
				}
			}
		}

		return changedBlocks;
	}
}
