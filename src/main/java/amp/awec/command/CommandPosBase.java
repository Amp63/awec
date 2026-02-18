package amp.awec.command;

import amp.awec.BlockPos;
import amp.awec.util.PosHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.util.phys.Vec3;

import java.util.function.Consumer;

public class CommandPosBase {

	@SuppressWarnings("unchecked")
	public static void register(CommandDispatcher<CommandSource> dispatcher, String command, int cornerNumber, Consumer<BlockPos> setter) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal(command)
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					Player player = source.getSender();
					if (player != null) {
						BlockPos pos = PosHelper.getPlayerBlockPos(player);
						setter.accept(pos);
						source.sendMessage("Corner " + cornerNumber + " set to " + pos);
						return 1;
					}
					return 0;
				})
				.then(ArgumentBuilderRequired.argument("position", ArgumentTypeIntegerCoordinates.intCoordinates())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						IntegerCoordinates coordinates = context.getArgument("position", IntegerCoordinates.class);
						int x = coordinates.getX(source);
						int y = coordinates.getY(source, true);
						int z = coordinates.getZ(source);
						BlockPos pos = new BlockPos(x, y, z);
						setter.accept(pos);
						source.sendMessage("Corner " + cornerNumber + " set to " + pos);
						return 1;
				}))
		);
	}
}
