package amp.awec.command.undoredo;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandRedo implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		CommandUndoBase.register(dispatcher, "/redo",
			(w, d) -> d.undoHistory.redo(w),
			"Redid %d edits", "Nothing to redo"
		);
	}
}
