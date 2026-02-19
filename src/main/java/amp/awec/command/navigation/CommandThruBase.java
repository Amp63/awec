package amp.awec.command.navigation;

import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.ThroughFinder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

import java.util.function.Function;

public class CommandThruBase {
	static final double MAX_RAY_DISTANCE = 50.0;
	static final double MARCH_DISTANCE = 1.0;
	static final double MAX_THRU_DISTANCE = 50.0;

	@SuppressWarnings("unchecked")
	public static void register(CommandDispatcher<CommandSource> dispatcher, String command, String wallCountName, Function<Player, Vec3> viewAngleFunction) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal(command)
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					doThru(source, 1, viewAngleFunction);
					return 1;
				})
				.then(ArgumentBuilderRequired.argument(wallCountName, ArgumentTypeInteger.integer(1, 255))
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						int wallCount = context.getArgument(wallCountName, Integer.class);
						doThru(source, wallCount, viewAngleFunction);
						return 1;
					})
				)
		);
	}

	private static void doThru(CommandSource source, int wallCount, Function<Player, Vec3> viewAngleFunction) {
		Player player = source.getSender();
		if (player == null) {
			return;
		}

		World world = player.world;
		if (world == null) {
			return;
		}

		Vec3 direction = viewAngleFunction.apply(player);
		Vec3 throughPos = ThroughFinder.findSpace(
			world,
			player.getPosition(1.0f, true),
			direction,
			wallCount, MAX_RAY_DISTANCE, MAX_THRU_DISTANCE, MARCH_DISTANCE
		);

		if (throughPos == null) {
			source.sendMessage("Could not find a valid location");
			return;
		}

		double playerTeleportY = throughPos.y;
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			playerTeleportY += player.getHeightOffset();
		}
		source.teleportPlayerToPos(player, throughPos.x, playerTeleportY, throughPos.z);
	}
}
