package amp.awec.command.info;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeWorldEditCommandName;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.HelpList;
import amp.awec.util.MessageHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;

public class CommandHelp implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/help")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("command_name", ArgumentTypeWorldEditCommandName.command())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						CommandPlayerData playerData = CommandPlayerData.get(source, false);
						if (playerData == null) {
							return 0;
						}

						String commandName = context.getArgument("command_name", String.class);
						HelpList.CommandHelpData helpData = HelpList.commandMap.get(commandName);
						if (helpData == null) {
							MessageHelper.error(source, "Unrecognized WorldEdit command");
							return 0;
						}

						playerData.player.sendMessage(TextFormatting.GRAY + " -- " + TextFormatting.ORANGE + "//" + commandName + TextFormatting.GRAY + " -- ");
						playerData.player.sendMessage("  " + helpData.description);
						playerData.player.sendMessage("  Usage: " + TextFormatting.LIGHT_GRAY + helpData.usage);

						if (helpData.examples.length > 0) {
							playerData.player.sendMessage("  Examples:");
							for (String example : helpData.examples) {
								playerData.player.sendMessage("    - " + TextFormatting.LIGHT_GRAY + example);
							}
						}

						return 1;
					})
				)
		);
	}
}
