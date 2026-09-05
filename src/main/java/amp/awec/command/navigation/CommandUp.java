package amp.awec.command.navigation;

import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.PosHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.joml.Vector3dc;

public class CommandUp implements CommandManager.CommandRegistry {
	private static final Block<?> UP_BLOCK = Blocks.GLASS;

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("up")
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
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
			Vector3dc playerPos = player.getPosition(1.0f, false);
			TilePos blockPos = PosHelper.getPlayerTilePos(player);
			double playerTeleportY = Math.max(0, Math.min(255, blockPos.y + distance));
			blockPos.y += distance - 1;

			source.teleportPlayerToPos(player, playerPos.x(), playerTeleportY, playerPos.z());
			world.setBlockTypeNotify(blockPos, UP_BLOCK);
		}
	}
}
