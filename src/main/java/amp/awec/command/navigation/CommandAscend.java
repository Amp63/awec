package amp.awec.command.navigation;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.phys.Vec3;

public class CommandAscend implements CommandManager.CommandRegistry {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		CommandThruBase.register(dispatcher, "ascend", "levels", p -> Vec3.getTempVec3(0, 1, 0));
	}
}
