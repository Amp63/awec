package amp.awec.command;

import amp.awec.ModState;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandPos2 implements CommandManager.CommandRegistry {
	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		CommandPosBase.register(dispatcher, "/pos2", 2, pos -> ModState.corner2 = pos);
	}
}
