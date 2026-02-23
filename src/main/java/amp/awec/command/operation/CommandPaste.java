package amp.awec.command.operation;

import amp.awec.command.CommandPlayerData;
import amp.awec.operation.WorldChange;
import amp.awec.util.MessageHelper;
import amp.awec.util.Vec3i;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.PosHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
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
					CommandPlayerData playerData = CommandPlayerData.get(source, false);
					if (playerData == null) {
						return 0;
					}

					Vec3i pastePos = PosHelper.getPlayerBlockPos(playerData.player);
					doPaste(playerData.world, pastePos, playerData.data);
					MessageHelper.success(source, "Pasted");

					return 1;
				})
		);
	}

	private void doPaste(World world, Vec3i pastePos, PlayerData playerData) {
		Vec3i setPos = pastePos.add(playerData.copyOffset);
		WorldChange result = playerData.clipboardBuffer.setAt(world, setPos, true);
		playerData.getUndoHistory(world).add(result);
	}
}
