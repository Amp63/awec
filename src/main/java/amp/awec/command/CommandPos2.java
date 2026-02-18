package amp.awec.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandPos2 implements CommandManager.CommandRegistry {
	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		CommandPosBase.register(dispatcher, "/pos2", 2, (data, pos) -> data.corner2 = pos);
	}
}
