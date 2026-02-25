package amp.awec.command.operation;
import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeBlockMask;
import amp.awec.operation.WorldChange;
import amp.awec.operation.SetOperation;
import amp.awec.command.argtypes.ArgumentTypeBlockPattern;
import amp.awec.pattern.BlockMask;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.pattern.BlockPattern;
import amp.awec.util.MessageHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import org.jetbrains.annotations.Nullable;

public class CommandSet implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/set")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("pattern", ArgumentTypeBlockPattern.pattern())
					.executes(context -> {
						return handleSetCommand(context, null);
					})
					.then(ArgumentBuilderLiteral.literal("-m")
						.then(ArgumentBuilderRequired.argument("mask", ArgumentTypeBlockMask.mask())
							.executes(context -> {
							BlockMask mask = context.getArgument("mask", BlockMask.class);
							return handleSetCommand(context, mask);
							})
						)
					)
				));
	}

	public int handleSetCommand(CommandContext<Object> context, @Nullable BlockMask mask) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);
		if (playerData == null) {
			return 0;
		}

		BlockPattern pattern = context.getArgument("pattern", BlockPattern.class);
		WorldChange result = SetOperation.execute(playerData.world, playerData.getSelection(), pattern, mask);
		playerData.addUndoChange(result);

		MessageHelper.info(source, "Changed " + result.changedBlockCount + " blocks");

		return 1;
	}
}
