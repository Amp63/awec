package amp.awec.command.operation;
import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeBlockMask;
import amp.awec.operation.WorldChange;
import amp.awec.operation.ReplaceOperation;
import amp.awec.command.argtypes.ArgumentTypeBlockPattern;
import amp.awec.pattern.BlockMask;
import amp.awec.pattern.BlockPattern;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.MessageHelper;
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
				.then(ArgumentBuilderRequired.argument("mask", ArgumentTypeBlockMask.mask())
				.then(ArgumentBuilderRequired.argument("replace_pattern", ArgumentTypeBlockPattern.pattern())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						CommandPlayerData playerData = CommandPlayerData.get(source);
						if (playerData == null) {
							return 0;
						}

						BlockMask mask = context.getArgument("mask", BlockMask.class);
						BlockPattern replaceWithPattern = context.getArgument("replace_pattern", BlockPattern.class);

						WorldChange result = ReplaceOperation.execute(playerData.world, playerData.getSelection(), mask, replaceWithPattern);
						playerData.addUndoChange(result);

						MessageHelper.info(source, "Changed " + result.changedBlockCount + " blocks");
						return 1;
					})
				)));
	}
}
