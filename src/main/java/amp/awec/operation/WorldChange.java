package amp.awec.operation;

import amp.awec.util.BlockChange;
import net.minecraft.core.world.World;

import java.util.LinkedList;
import java.util.List;

public class WorldChange {
	public List<BlockChange> changedBlocks = new LinkedList<>();
	public int changedBlockCount = 0;

	public void addChange(BlockChange blockChange) {
		changedBlocks.add(blockChange);
		changedBlockCount++;
	}

	public void extend(WorldChange other) {
		this.changedBlocks.addAll(other.changedBlocks);
		this.changedBlockCount += other.changedBlockCount;
	}

	public WorldChange apply(World world) {
		WorldChange oldWorld = new WorldChange();

		for (BlockChange blockChange : changedBlocks) {
			BlockChange oldBlock = blockChange.apply(world);
			oldWorld.addChange(oldBlock);
		}

		return oldWorld;
	}

	@Override
	public String toString() {
		return "OpResult(" + changedBlockCount + " changed blocks)";
	}
}
