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
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.function.Function;

public class CommandThruBase {
	@SuppressWarnings("unchecked")
	public static void register(CommandDispatcher<CommandSource> dispatcher, String command,
								String wallCountName, Function<Player, Vector3dc> viewAngleFunction, Vector3dc teleportOffset) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal(command)
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					doThru(context, 1, viewAngleFunction, teleportOffset);
					return 1;
				})
				.then(ArgumentBuilderRequired.argument(wallCountName, ArgumentTypeInteger.integer(1, 255))
					.executes(context -> {
						int wallCount = context.getArgument(wallCountName, Integer.class);
						doThru(context, wallCount, viewAngleFunction, teleportOffset);
						return 1;
					})
				)
		);
	}

	private static void doThru(CommandContext<Object> context, int wallCount, Function<Player, Vector3dc> viewAngleFunction, Vector3dc teleportOffset) {
		CommandSource source = (CommandSource) context.getSource();
		Player player = source.getSender();
		if (player == null) {
			return;
		}

		World world = player.world;

		Vector3dc direction = viewAngleFunction.apply(player);
		Vector3dc startPos = player.getPosition(1.0f, true);
		Vector3dc throughPos = ThroughFinder.findSpace(
			world, new Vector3d(startPos), new Vector3d(direction), wallCount,
			Config.THRU_MAX_RAY_DISTANCE, Config.THRU_MAX_THRU_DISTANCE, Config.THRU_MARCH_DISTANCE
		);

		if (throughPos == null) {
			MessageHelper.error(source, "Could not find a valid location");
			return;
		}

		Vector3d teleportPos = new Vector3d(throughPos).add(teleportOffset);
		source.teleportPlayerToPos(player, teleportPos.x(), teleportPos.y(), teleportPos.z());
	}
}
