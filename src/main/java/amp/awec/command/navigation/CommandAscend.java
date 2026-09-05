package amp.awec.command.navigation;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import org.joml.Vector3d;

public class CommandAscend implements CommandManager.CommandRegistry {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		CommandThruBase.register(dispatcher, "ascend", "levels", p -> new Vector3d(0, 1, 0), new Vector3d(0, 0, 0));
	}
}
