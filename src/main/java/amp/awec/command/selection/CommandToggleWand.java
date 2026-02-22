package amp.awec.command.selection;

import amp.awec.WorldEditMod;
import amp.awec.command.CommandPlayerData;
import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import amp.awec.permission.WorldEditPermissions;
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
					CommandPlayerData playerData = CommandPlayerData.get(source, false);
					if (playerData == null) {
						return 0;
					}

					if (playerData.data.wandEnabled) {
						playerData.data.wandEnabled = false;
						source.sendMessage("Disabled edit wand");
					}
					else {
						playerData.data.wandEnabled = true;
						source.sendMessage("Enabled edit wand");
					}

					return 1;
				})
		);
	}
}
