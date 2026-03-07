package amp.awec.command.selection;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeBlockMask;
import amp.awec.pattern.BlockMask;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.BlockState;
import amp.awec.util.DistributionHelper;
import amp.awec.util.MessageHelper;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolume;
import amp.awec.volume.CuboidVolumeIterator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandTrim implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/trim")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					try {
						BlockMask mask = new BlockMask("!air", null);
						return handleTrimCommand(context, mask);
					} catch (BlockMask.BlockMaskException e) {
						MessageHelper.error((CommandSource) context.getSource(), e.getMessage());
						return 0;
					}
				})
				.then(ArgumentBuilderRequired.argument("mask", ArgumentTypeBlockMask.mask())
					.executes(context -> {
						BlockMask mask = context.getArgument("mask", BlockMask.class);
						return handleTrimCommand(context, mask);
					})
				)
		);
	}

	public int handleTrimCommand(CommandContext<Object> context, BlockMask mask) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);
		if (playerData == null) {
			return 0;
		}

		CuboidVolume selection = playerData.getSelection();
		Vec3i newMin = new Vec3i(selection.getMinCorner());
		Vec3i newMax = new Vec3i(selection.getMaxCorner());

		CuboidVolumeIterator iterator = new CuboidVolumeIterator(selection);
		while (iterator.hasNext()) {
			Vec3i pos = iterator.next();
			BlockState blockState = new BlockState(playerData.world, pos);
			if (mask.matches(blockState)) {
				if (pos.x > newMin.x) newMin.x = pos.x;
				if (pos.y > newMin.y) newMin.y = pos.y;
				if (pos.z > newMin.z) newMin.z = pos.z;

				if (pos.x < newMax.x) newMax.x = pos.x;
				if (pos.y < newMax.y) newMax.y = pos.y;
				if (pos.z < newMax.z) newMax.z = pos.z;
			}
		}

		selection.setCorner1(newMin);
		selection.setCorner2(newMax);

		MessageHelper.success(playerData.player, "Trimmed selection");

		return 1;
	}
}
