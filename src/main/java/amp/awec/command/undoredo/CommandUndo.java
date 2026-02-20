package amp.awec.command.undoredo;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandUndo implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		CommandUndoBase.register(dispatcher, "/undo",
			(w, d) -> d.undoHistory.undo(w),
			"Undid %d edits", "Nothing to undo"
		);
	}
}
