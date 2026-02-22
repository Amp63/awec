package amp.awec.command.selection;

import amp.awec.command.CommandPlayerData;
import amp.awec.data.PlayerDataManager;
import amp.awec.util.Vec3i;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.PosHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;

import java.util.function.BiConsumer;

public class CommandPosBase {

	@SuppressWarnings("unchecked")
	public static void register(CommandDispatcher<CommandSource> dispatcher, String command, int cornerNumber, BiConsumer<CommandPlayerData, Vec3i> setter) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal(command)
				.requires(source -> WorldEditPermissions.canUseWorldEdit((CommandSource) source))
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					CommandPlayerData playerData = CommandPlayerData.get(source, false);
					if (playerData == null) {
						return 0;
					}

					Vec3i pos = PosHelper.getPlayerBlockPos(playerData.player);
					setter.accept(playerData, pos);
					source.sendMessage("Corner " + cornerNumber + " set to " + pos);

					return 1;
				})
				.then(ArgumentBuilderRequired.argument("position", ArgumentTypeIntegerCoordinates.intCoordinates())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						CommandPlayerData playerData = CommandPlayerData.get(source, false);
						if (playerData == null) {
							return 0;
						}

						IntegerCoordinates coordinates = context.getArgument("position", IntegerCoordinates.class);
						int x = coordinates.getX(source);
						int y = coordinates.getY(source, true);
						int z = coordinates.getZ(source);

						Vec3i pos = new Vec3i(x, y, z);
						setter.accept(playerData, pos);
						source.sendMessage("Corner " + cornerNumber + " set to " + pos);

						return 1;
				}))
		);
	}
}
