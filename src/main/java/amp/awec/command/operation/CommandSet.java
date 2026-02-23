package amp.awec.command.operation;
import amp.awec.command.CommandPlayerData;
import amp.awec.operation.WorldChange;
import amp.awec.operation.SetOperation;
import amp.awec.command.argtypes.ArgumentTypePattern;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.pattern.BlockPattern;
import amp.awec.util.MessageHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandSet implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/set")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("pattern", ArgumentTypePattern.normal())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						CommandPlayerData playerData = CommandPlayerData.get(source);
						if (playerData == null) {
							return 0;
						}

						BlockPattern pattern = context.getArgument("pattern", BlockPattern.class);
						WorldChange result = SetOperation.execute(playerData.world, playerData.getSelection(), pattern);
						playerData.addUndoChange(result);

						MessageHelper.info(source, "Changed " + result.changedBlockCount + " blocks");
						return 1;
					})
				));
	}
}
