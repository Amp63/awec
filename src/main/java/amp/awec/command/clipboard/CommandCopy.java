package amp.awec.command.clipboard;

import amp.awec.command.CommandPlayerData;
import amp.awec.util.MessageHelper;
import amp.awec.util.Vec3i;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.volume.CuboidVolume;
import amp.awec.util.PosHelper;
import amp.awec.volume.CuboidVolumeBuffer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
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
					CommandPlayerData playerData = CommandPlayerData.get(source);
					if (playerData == null) {
						return 0;
					}

					CuboidVolume selection = playerData.getSelection();
					Vec3i copyPos = PosHelper.getPlayerBlockPos(playerData.player);
					doCopy(playerData.world, selection, copyPos, playerData.data);
					MessageHelper.info(source, "Copied");

					return 1;
				})
		);
	}

	private void doCopy(World world, CuboidVolume volume, Vec3i copyPos, PlayerData playerData) {
		playerData.clipboardBuffer = CuboidVolumeBuffer.copyFrom(world, volume);
		Vec3i rootPos = volume.getMinCorner();
		playerData.copyOffset = rootPos.subtract(copyPos);
	}
}
