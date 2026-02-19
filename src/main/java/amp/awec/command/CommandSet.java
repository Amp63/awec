package amp.awec.command;
import amp.awec.BlockPos;
import amp.awec.BlockVolumeIterator;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permissions.WorldEditPermissions;
import amp.awec.util.BlockPattern;
import amp.awec.util.BlockPatternException;
import amp.awec.util.BlockState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
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
				.then(ArgumentBuilderRequired.argument("pattern", ArgumentTypeString.greedyString())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						Player player = source.getSender();
						if (player == null) {
							return 0;
						}
						String patternString = (String) context.getArgument("pattern", String.class);
						BlockPattern pattern;
						try {
							pattern = new BlockPattern(patternString);
						}
						catch (BlockPatternException e) {
							source.sendMessage(e.getMessage());
							return 0;
						}
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
						doSet(world, playerData.corner1, playerData.corner2, pattern);
						return 1;
					})
				));
	}

	private void doSet(World world, BlockPos corner1, BlockPos corner2, BlockPattern pattern) {
		BlockVolumeIterator iterator = new BlockVolumeIterator(corner1, corner2);
		while (iterator.hasNext()) {
			BlockPos setPos = iterator.next();
			BlockState sampledBlock = pattern.sample();
			if (sampledBlock != null) {
				sampledBlock.setNotify(world, setPos);
			}
		}
	}
}
