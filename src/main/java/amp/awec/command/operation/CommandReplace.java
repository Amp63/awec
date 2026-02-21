package amp.awec.command.operation;
import amp.awec.command.CommandPlayerData;
import amp.awec.operation.WorldChange;
import amp.awec.operation.ReplaceOperation;
import amp.awec.command.argtypes.ArgumentTypePattern;
import amp.awec.pattern.BlockPattern;
import amp.awec.permission.WorldEditPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandReplace implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/replace")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("target_pattern", ArgumentTypePattern.replace())
				.then(ArgumentBuilderRequired.argument("replace_pattern", ArgumentTypePattern.normal())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						CommandPlayerData playerData = CommandPlayerData.get(source);
						if (playerData == null) {
							return 0;
						}

						BlockPattern targetPattern = context.getArgument("target_pattern", BlockPattern.class);
						BlockPattern replaceWithPattern = context.getArgument("replace_pattern", BlockPattern.class);

						WorldChange result = ReplaceOperation.execute(playerData.world, playerData.getSelection(), targetPattern, replaceWithPattern);
						playerData.addUndoChange(result);

						source.sendMessage("Changed " + result.changedBlockCount + " blocks");
						return 1;
					})
				)));
	}
}
