package amp.awec.command;

import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permissions.WorldEditPermissions;
import amp.awec.util.WandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandToggleWand implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/togglewand")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					Player player = source.getSender();
					if (player == null) {
						return 0;
					}

					PlayerData playerData = WorldEditMod.getPlayerData(player);
					if (playerData == null) {
						return 0;
					}

					if (playerData.wandEnabled) {
						playerData.wandEnabled = false;
						source.sendMessage("Disabled edit wand");
					}
					else {
						playerData.wandEnabled = true;
						source.sendMessage("Enabled edit wand");
					}

					return 1;
				})
		);
	}
}
