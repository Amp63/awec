package amp.awec.command;
import amp.awec.ModState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeIntegerCoordinates;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import org.useless.seedviewer.collections.BlockPos3D;

public class CommandPos2 implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/pos2")
//				.requires(CommandSource::hasAdmin)
				.then(ArgumentBuilderRequired.argument("position", ArgumentTypeIntegerCoordinates.intCoordinates())
				.executes(context -> {
					CommandSource source = (CommandSource) context.getSource();
					IntegerCoordinates coordinates = context.getArgument("position", IntegerCoordinates.class);
					int x = coordinates.getX(source);
					int y = coordinates.getY(source, true);
					int z = coordinates.getZ(source);
					ModState.corner2 = new BlockPos3D(x, y, z);
					source.sendMessage("Corner 2 set to " + x + ", " + y + ", " + z);
					return 1;
				})
		));
	}
}
