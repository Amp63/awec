package amp.awec.command.clipboard;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeBlockMask;
import amp.awec.operation.WorldChange;
import amp.awec.pattern.BlockMask;
import amp.awec.util.MessageHelper;
import amp.awec.util.Vec3i;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.PosHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import org.jetbrains.annotations.Nullable;

public class CommandPaste implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/paste")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.requires(source -> WorldEditPermissions.hasClipboard((CommandSource) source))
				.executes(context -> {
					return handlePasteCommand(context, null);
				})
				.then(ArgumentBuilderLiteral.literal("-m")
					.then(ArgumentBuilderRequired.argument("world_mask", ArgumentTypeBlockMask.mask())
						.executes(context -> {
							BlockMask worldMask = context.getArgument("world_mask", BlockMask.class);
							return handlePasteCommand(context, worldMask);
						})
					)
				)
		);
	}

	private int handlePasteCommand(CommandContext<Object> context, @Nullable BlockMask mask) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source, false);
		if (playerData == null) {
			return 0;
		}

		Vec3i pastePos = PosHelper.getPlayerBlockPos(playerData.player);
		MessageHelper.success(source, "Pasted");

		Vec3i setPos = pastePos.add(playerData.data.copyOffset);
		WorldChange result = playerData.data.clipboardBuffer.setAt(playerData.world, setPos, mask);
		playerData.addUndoChange(result);

		return 1;
	}
}
