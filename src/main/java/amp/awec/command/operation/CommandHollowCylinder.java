package amp.awec.command.operation;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypePattern;
import amp.awec.operation.CylinderOperation;
import amp.awec.operation.HollowCylinderOperation;
import amp.awec.operation.WorldChange;
import amp.awec.pattern.BlockPattern;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.MessageHelper;
import amp.awec.util.PosHelper;
import amp.awec.util.Vec3i;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandHollowCylinder implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/hcyl")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("pattern", ArgumentTypePattern.normal())
					.then(ArgumentBuilderRequired.argument("radius", ArgumentTypeInteger.integer(1, 255))
						.executes(context -> {
							return handleCylinderCommand(context, 1);
						})
						.then(ArgumentBuilderRequired.argument("height", ArgumentTypeInteger.integer(1, 255))
							.executes(context -> {
								int height = context.getArgument("height", Integer.class);
								return handleCylinderCommand(context, height);
							})
						)
					)
				));
	}

	private int handleCylinderCommand(CommandContext<Object> context, int height) {
		CommandSource source = (CommandSource) context.getSource();

		CommandPlayerData playerData = CommandPlayerData.get(source, false);
		if (playerData == null) {
			return 0;
		}

		BlockPattern pattern = context.getArgument("pattern", BlockPattern.class);
		int radius = context.getArgument("radius", Integer.class);

		Vec3i centerPos = PosHelper.getPlayerBlockPos(playerData.player);
		WorldChange result = HollowCylinderOperation.execute(playerData.world, centerPos, pattern, radius, height);
		playerData.addUndoChange(result);

		MessageHelper.info(source, "Changed " + result.changedBlockCount + " blocks");

		return 1;
	}
}
