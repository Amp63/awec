package amp.awec.command.selection;

import amp.awec.command.CommandPlayerData;
import amp.awec.data.ClientPlayerData;
import amp.awec.permission.WorldEditPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

@Environment(EnvType.CLIENT)
public class CommandDrawSel implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/drawsel")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();

					ClientPlayerData.drawSelections = !ClientPlayerData.drawSelections;

					return 1;
				})
		);
	}
}
