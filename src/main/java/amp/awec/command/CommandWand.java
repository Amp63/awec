package amp.awec.command;

import amp.awec.permissions.WorldEditPermissions;
import amp.awec.util.WandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandWand implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/wand")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					Player player = source.getSender();
					if (player != null) {
						player.inventory.insertItem(WandHelper.WAND_ITEM.getDefaultStack(), true);
						return 1;
					}
					return 0;
				})
		);
	}
}
