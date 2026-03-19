package amp.awec.command.operation;
import amp.awec.WorldEditMod;
import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeDyeColor;
import amp.awec.operation.PaintOperation;
import amp.awec.operation.SetOperation;
import amp.awec.operation.WorldChange;
import amp.awec.pattern.BlockMask;
import amp.awec.pattern.BlockPattern;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.MessageHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.helper.DyeColor;

public class CommandPaint implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/paint")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.then(ArgumentBuilderRequired.argument("color", ArgumentTypeDyeColor.color())
					.executes(context -> {
						DyeColor color = context.getArgument("color", DyeColor.class);
						return handlePaintCommand(context, color, BlockMask.ANY);
					})
					.then(ArgumentBuilderRequired.argument("set_color", ArgumentTypeDyeColor.color()).executes(
						context -> {
							DyeColor maskColor = context.getArgument("color", DyeColor.class);
							DyeColor color = context.getArgument("set_color", DyeColor.class);
							try {
								BlockMask mask = new BlockMask("c." + maskColor.colorID, null);
								return handlePaintCommand(context, color, mask);
							} catch (BlockMask.BlockMaskException e) {
								throw new RuntimeException(e);
							}
						}
					))
				)
		);
	}

	public int handlePaintCommand(CommandContext<Object> context, DyeColor dyeColor, BlockMask mask) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);
		if (playerData == null) {
			return 0;
		}

		WorldChange result = PaintOperation.execute(playerData.world, playerData.getSelection(), dyeColor, mask.and(playerData.data.globalMask));
		playerData.addUndoChange(result);

		MessageHelper.info(source, "Changed " + result.changedBlockCount + " blocks");

		return 1;
	}
}
