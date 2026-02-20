package amp.awec.data;

import amp.awec.WorldEditMod;
import amp.awec.operation.OperationResult;
import amp.awec.util.Vec3i;
import amp.awec.volume.CopiedVolume;
import net.minecraft.core.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class UndoHistory {
	private static final int MAX_UNDO_HISTORY = 50;

	private final List<OperationResult> undoTape = new LinkedList<>();
	private ListIterator<OperationResult> head = undoTape.listIterator();

	public void add(OperationResult entry) {
		// Remove subsequent entries
		while (head.hasNext()) {
			head.next();
			head.remove();
		}

		// Add the new entry
		if (undoTape.size() >= MAX_UNDO_HISTORY) {
			undoTape.remove(0);
		}

		undoTape.add(entry);
		head = undoTape.listIterator(undoTape.size());
	}

	public boolean undo(World world) {
		if (head.hasPrevious()) {
			boolean atEnd = head.hasNext();
			OperationResult entry = head.previous();
			if (atEnd) {
				entry.previousVolume.setAt(world, entry.previousVolumePos, false);
			}
			else {
				// Save current state and add to the end
				head.next();  // Next to prevent new entry from being deleted
				OperationResult result = entry.previousVolume.setAt(world, entry.previousVolumePos, true);
				this.add(result);
				head.previous();
				head.previous();  // Step back to where we were before
			}
			return true;
		}

		return false;
	}

	public boolean redo(World world) {
		if (head.hasNext()) {
			if (!head.hasPrevious()) {
				head.next();
				if (!head.hasNext()) {
					return false;
				}
			}
			OperationResult entry = head.next();
			entry.previousVolume.setAt(world, entry.previousVolumePos, false);
			if (!head.hasNext()) {
				// Remove current state entry
				this.head.remove();
			}
			return true;
		}

		return false;
	}
}
