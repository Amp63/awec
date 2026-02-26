package amp.awec.command.mask;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeBlockMask;
import amp.awec.pattern.BlockMask;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.MessageHelper;
import amp.awec.util.WandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import org.jetbrains.annotations.Nullable;

public class CommandGmask implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/gmask")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					return handleGmaskCommand(context, null);
				})
				.then(ArgumentBuilderRequired.argument("mask", ArgumentTypeBlockMask.mask())
					.executes(context -> {
						BlockMask mask = context.getArgument("mask", BlockMask.class);
						return handleGmaskCommand(context, mask);
					})
				)
		);
	}

	private int handleGmaskCommand(CommandContext<Object> context, @Nullable BlockMask mask) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);
		if (playerData == null) {
			return 0;
		}

		if (mask == null) {
			playerData.data.globalMask = BlockMask.ANY;
			MessageHelper.info(source, "Cleared global mask");
		}
		else {
			playerData.data.globalMask = mask;
			MessageHelper.info(source, "Global mask set");
		}

		return 1;
	}
}
