package amp.awec.command.info;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeBlockMask;
import amp.awec.pattern.BlockMask;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.DistributionHelper;
import amp.awec.util.MessageHelper;
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

public class CommandCount implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/count")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					return handleCountCommand(context, BlockMask.ANY);
				})
				.then(ArgumentBuilderRequired.argument("mask", ArgumentTypeBlockMask.mask())
					.executes(context -> {
						BlockMask mask = context.getArgument("mask", BlockMask.class);
						return handleCountCommand(context, mask);
					})
				)
		);
	}

	public int handleCountCommand(CommandContext<Object> context, BlockMask mask) {
		CommandSource source = (CommandSource) context.getSource();
		CommandPlayerData playerData = CommandPlayerData.get(source);
		if (playerData == null) {
			return 0;
		}

		// Get distribution
		Map<Block<?>, Long> distribution = DistributionHelper.getDistribution(
			playerData.world,
			playerData.getSelection(),
			mask.and(playerData.data.globalMask)
		);

		// Count total blocks
		long totalBlocks = distribution.values().stream()
			.mapToLong(Long::longValue)
			.sum();

		MessageHelper.info(playerData.player, totalBlocks + " matching blocks");

		return 1;
	}
}
