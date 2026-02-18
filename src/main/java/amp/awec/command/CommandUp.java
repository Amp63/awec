package amp.awec.command;

import amp.awec.BlockPos;
import amp.awec.util.PosHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

public class CommandUp implements CommandManager.CommandRegistry {
	private static final int UP_BLOCK_ID = Blocks.GLASS.id();

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("up")
				.requires(source -> ((CommandSource)source).hasAdmin())
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					doUp(source, 1);
					return 1;
				})
				.then(ArgumentBuilderRequired.argument("distance", ArgumentTypeInteger.integer(-255, 255))
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						int distance = context.getArgument("distance", Integer.class);
						doUp(source, distance);
						return 1;
					})
				)
		);
	}

	private void doUp(CommandSource source, int distance) {
		Player player = source.getSender();
		if (player != null) {
			World world = source.getWorld();
			Vec3 playerPos = player.getPosition(1.0f, false);
			BlockPos blockPos = PosHelper.getPlayerBlockPos(player);
			int shiftedY = Math.max(0, Math.min(255, blockPos.y + distance));
			double playerTeleportY = shiftedY + 1.1;
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				playerTeleportY += player.getHeightOffset();
			}

			source.teleportPlayerToPos(player, playerPos.x, playerTeleportY, playerPos.z);
			world.setBlockWithNotify(blockPos.x, shiftedY, blockPos.z, UP_BLOCK_ID);
		}
	}
}
