package amp.awec.command;

import amp.awec.ModState;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandPos1 implements CommandManager.CommandRegistry {
	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		CommandPosBase.register(dispatcher, "/pos1", 1, pos -> ModState.corner1 = pos);
	}
}
