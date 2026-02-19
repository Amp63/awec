package amp.awec.command.permission;

import amp.awec.permission.WorldEditWhitelist;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

@Environment(EnvType.SERVER)
public class CommandWorldEditWhitelist implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("wewhitelist")
				.requires(source -> ((CommandSource)source).hasAdmin())
				.then(ArgumentBuilderLiteral.literal("add")
					.then(ArgumentBuilderRequired.argument("name", ArgumentTypeString.word())
						.executes(context -> {
							CommandSource source = (CommandSource) context.getSource();
							String nameToAdd = context.getArgument("name", String.class);
							Player player = source.getWorld().getPlayerEntityByName(nameToAdd);
							if (player == null) {
								source.sendMessage("Could not find player " + nameToAdd);
								return 0;
							}
							if (WorldEditWhitelist.addUUID(player.uuid)) {
								source.sendMessage(player.username + " can now use WorldEdit");
								return 1;
							}
							else {
								source.sendMessage(player.username + " is already whitelisted");
								return 0;
							}
						})
					)
				)
				.then(ArgumentBuilderLiteral.literal("remove")
					.then(ArgumentBuilderRequired.argument("name", ArgumentTypeString.word())
					.executes(context -> {
							CommandSource source = (CommandSource) context.getSource();
							String nameToRemove = context.getArgument("name", String.class);
							Player player = source.getWorld().getPlayerEntityByName(nameToRemove);
							if (player == null) {
								source.sendMessage("Could not find player " + nameToRemove);
								return 0;
							}
							if (WorldEditWhitelist.removeUUID(player.uuid)) {
								source.sendMessage(player.username + " can no longer use WorldEdit");
								return 1;
							}
							else {
								source.sendMessage(player.username + " is not whitelisted");
								return 0;
							}
						})
					)
				)
		);
	}
}
