package amp.awec.command.config;

import amp.awec.WorldEditMod;
import amp.awec.config.Config;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.MessageHelper;
import amp.awec.util.WandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

import java.io.IOException;

public class CommandReloadConfig implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/reloadconfig")
				.requires(source -> ((CommandSource)source).hasAdmin())
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					try {
						Config.load();

						MessageHelper.success(source, "Reloaded configuration");
						return 1;
					}
					catch (IOException e) {
						WorldEditMod.LOGGER.error(e.toString());
						MessageHelper.error(source, "Failed to reload configuration");
						return 0;
					}
				})
		);
	}
}
