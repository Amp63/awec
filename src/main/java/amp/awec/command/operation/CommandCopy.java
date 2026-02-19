package amp.awec.command.operation;

import amp.awec.util.Vec3i;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.CuboidVolume;
import amp.awec.util.PosHelper;
import amp.awec.volume.CopiedVolume;
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
					if (!playerData.selection.isComplete()) {
						source.sendMessage("Both corners must be set");
						return 0;
					}

					World world = source.getWorld();
					Vec3i copyPos = PosHelper.getPlayerBlockPos(player);
					doCopy(world, playerData.selection, copyPos, playerData);
					source.sendMessage("Copied");
					return 1;
				})
		);
	}

	private void doCopy(World world, CuboidVolume volume, Vec3i copyPos, PlayerData playerData) {
		playerData.clipboardVolume = new CopiedVolume(world, volume);
		Vec3i rootPos = volume.getMinCorner();
		playerData.copyOffset = rootPos.subtract(copyPos);
	}
}
