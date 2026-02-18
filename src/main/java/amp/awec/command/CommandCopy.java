package amp.awec.command;

import amp.awec.BlockPos;
import amp.awec.BlockVolumeIterator;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.util.PosHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;

public class CommandCopy implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/copy")
				.requires(source -> ((CommandSource)source).hasAdmin())
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					Player player = source.getSender();
					if (player == null) {
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
					BlockPos copyPos = PosHelper.getPlayerBlockPos(player);
					doCopy(world, playerData.corner1, playerData.corner2, copyPos);
					return 1;
				})
		);
	}

	private void doCopy(World world, BlockPos corner1, BlockPos corner2, BlockPos copyPos) {
		BlockVolumeIterator iterator = new BlockVolumeIterator(corner1, corner2);
		while (iterator.hasNext()) {
			// TODO: Copy block data into playerData.clipboardBlocks
		}
	}
}
