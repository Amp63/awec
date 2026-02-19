package amp.awec.command;

import amp.awec.util.BlockPos;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permissions.WorldEditPermissions;
import amp.awec.util.PosHelper;
import amp.awec.volume.CopiedVolume;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;

public class CommandPaste implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/paste")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
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
					BlockPos pastePos = PosHelper.getPlayerBlockPos(player);
					doPaste(world, pastePos, playerData);
					source.sendMessage("Pasted");
					return 1;
				})
		);
	}

	private void doPaste(World world, BlockPos pastePos, PlayerData playerData) {
		BlockPos setPos = pastePos.add(playerData.copyOffset);
		playerData.clipboardVolume.setAt(world, setPos);
	}
}
