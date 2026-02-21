package amp.awec.data;

import amp.awec.WorldEditMod;
import amp.awec.operation.WorldChange;
import net.minecraft.core.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class UndoHistory {
	private static final int MAX_UNDO_HISTORY = 50;

	private final List<WorldChange> undoTape = new LinkedList<>();
	private ListIterator<WorldChange> head = undoTape.listIterator();

	public void add(WorldChange change) {
		// Remove subsequent entries
		while (head.hasNext()) {
			head.next();
			head.remove();
		}

		// Add the new change
		if (undoTape.size() >= MAX_UNDO_HISTORY) {
			undoTape.remove(0);
		}

		undoTape.add(change);
		head = undoTape.listIterator(undoTape.size());
	}

	public boolean undo(World world) {
		if (head.hasPrevious()) {
			boolean atEnd = !head.hasNext();
			WorldChange change = head.previous();
			WorldChange overwritten = change.apply(world);

			if (atEnd) {
				// Save current state and add to the end
				head.next();  // Next to prevent new change from being deleted
				this.add(overwritten);
				head.previous();
				head.previous();  // Step back to where we were before
			}

			return true;
		}

		return false;
	}

	public boolean redo(World world) {
		if (head.hasNext()) {
			head.next();
			if (!head.hasNext()) {
				return false;
			}

			WorldChange change = head.next();
			change.apply(world);
			if (!head.hasNext()) {
				// Remove current change
				this.head.remove();
			}

			return true;
		}

		return false;
	}
}
