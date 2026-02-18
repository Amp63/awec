package amp.awec.command;

import amp.awec.BlockPos;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.util.PosHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.util.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommandPosBase {

	@SuppressWarnings("unchecked")
	public static void register(CommandDispatcher<CommandSource> dispatcher, String command, int cornerNumber, BiConsumer<PlayerData, BlockPos> setter) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal(command)
				.requires(source -> ((CommandSource)source).hasAdmin())
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					Player player = source.getSender();
					if (player != null) {
						PlayerData playerData = WorldEditMod.getPlayerData(player);
						BlockPos pos = PosHelper.getPlayerBlockPos(player);
						setter.accept(playerData, pos);
						source.sendMessage("Corner " + cornerNumber + " set to " + pos);
						return 1;
					}
					return 0;
				})
				.then(ArgumentBuilderRequired.argument("position", ArgumentTypeIntegerCoordinates.intCoordinates())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						Player player = source.getSender();
						if (player != null) {
							IntegerCoordinates coordinates = context.getArgument("position", IntegerCoordinates.class);
							int x = coordinates.getX(source);
							int y = coordinates.getY(source, true);
							int z = coordinates.getZ(source);
							PlayerData playerData = WorldEditMod.getPlayerData(player);
							BlockPos pos = new BlockPos(x, y, z);
							setter.accept(playerData, pos);
							source.sendMessage("Corner " + cornerNumber + " set to " + pos);
							return 1;
						}
						return 0;
				}))
		);
	}
}
