package amp.awec.command.info;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeBlockMask;
import amp.awec.pattern.BlockMask;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.DistributionHelper;
import amp.awec.util.MessageHelper;
import amp.awec.util.WandHelper;
import amp.awec.volume.CuboidVolume;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandDistr implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/distr")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					return handleDistrCommand(context, BlockMask.ANY);
				})
				.then(ArgumentBuilderRequired.argument("mask", ArgumentTypeBlockMask.mask())
					.executes(context -> {
						BlockMask mask = context.getArgument("mask", BlockMask.class);
						return handleDistrCommand(context, mask);
					})
				)
		);
	}

	public int handleDistrCommand(CommandContext<Object> context, BlockMask mask) {
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

		// Sort distribution entries by frequency descending
		List<Map.Entry<Block<?>, Long>> sortedEntries = distribution.entrySet().stream()
			.sorted(Map.Entry.<Block<?>, Long>comparingByValue().reversed())
			.collect(Collectors.toList());

		// Print results
		MessageHelper.info(playerData.player, "Selection Distribution:");
		for (Map.Entry<Block<?>, Long> entry : sortedEntries) {
			Block<?> block = entry.getKey();
			long frequency = entry.getValue();

			double percentage = (double) frequency / totalBlocks * 100.0;
			String blockName = block == null ? "air" : block.namespaceId().value().split("/")[1];

			String message = String.format("- %s: %s%.3f%% %s(%d/%d)%s", blockName, TextFormatting.LIME, percentage, TextFormatting.GRAY, frequency, totalBlocks, TextFormatting.RESET);
			playerData.player.sendMessage(message);
		}

		return 1;
	}
}
