package amp.awec.command.navigation;

import amp.awec.config.Config;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.MessageHelper;
import amp.awec.util.ThroughFinder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

import java.util.function.Function;

public class CommandThruBase {
	@SuppressWarnings("unchecked")
	public static void register(CommandDispatcher<CommandSource> dispatcher, String command, String wallCountName, Function<Player, Vec3> viewAngleFunction) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal(command)
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					doThru(context, 1, viewAngleFunction);
					return 1;
				})
				.then(ArgumentBuilderRequired.argument(wallCountName, ArgumentTypeInteger.integer(1, 255))
					.executes(context -> {
						int wallCount = context.getArgument(wallCountName, Integer.class);
						doThru(context, wallCount, viewAngleFunction);
						return 1;
					})
				)
		);
	}

	private static void doThru(CommandContext<Object> context, int wallCount, Function<Player, Vec3> viewAngleFunction) {
		CommandSource source = (CommandSource) context.getSource();
		Player player = source.getSender();
		if (player == null) {
			return;
		}

		World world = player.world;
		if (world == null) {
			return;
		}

		Vec3 direction = viewAngleFunction.apply(player);
		Vec3 startPos = player.getPosition(1.0f, true);
		Vec3 throughPos = ThroughFinder.findSpace(
			world, startPos, direction, wallCount,
			Config.THRU_MAX_RAY_DISTANCE, Config.THRU_MAX_THRU_DISTANCE, Config.THRU_MARCH_DISTANCE
		);

		if (throughPos == null) {
			MessageHelper.error(source, "Could not find a valid location");
			return;
		}

		double playerTeleportY = throughPos.y;
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			playerTeleportY += player.getHeightOffset();
		}
		source.teleportPlayerToPos(player, throughPos.x, playerTeleportY, throughPos.z);
	}
}
