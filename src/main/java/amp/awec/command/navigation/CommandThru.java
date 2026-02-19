package amp.awec.command.navigation;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandThru implements CommandManager.CommandRegistry {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		CommandThruBase.register(dispatcher, "thru", "walls", p -> p.getViewVector(1.0f));
	}
}
