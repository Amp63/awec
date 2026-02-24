package amp.awec.command.clipboard;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeDirection;
import amp.awec.operation.MoveOperation;
import amp.awec.operation.WorldChange;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.DirectionHelper;
import amp.awec.util.MessageHelper;
import amp.awec.util.Vec3i;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.helper.Direction;

public class CommandFlip implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/flip")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					return handleFlipCommand(context, null);
				})
				.then(ArgumentBuilderRequired.argument("direction", ArgumentTypeDirection.direction())
					.executes(context -> {
						Direction direction = context.getArgument("direction", Direction.class);
						return handleFlipCommand(context, direction);
					}))
				);
	}

	private int handleFlipCommand(CommandContext<Object> context, Direction direction) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);
		if (playerData == null) {
			return 0;
		}

		if (playerData.data.clipboardBuffer == null) {
			MessageHelper.error(source, "Clipboard is empty");
			return 0;
		}

		Vec3i flipVector;
		if (direction == null) {
			// Default to forward direction
			direction = DirectionHelper.getMajorDirection(playerData.player.xRot, playerData.player.yRot);
			flipVector = new Vec3i(direction.rotate(1));  // Rotate to make perpendicular
		}
		else {
			flipVector = new Vec3i(direction);
		}

		playerData.data.clipboardBuffer.flip(flipVector);

		MessageHelper.info(source, "Flipped clipboard");

		return 1;
	}
}
