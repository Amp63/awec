package amp.awec.operation;

import amp.awec.util.BlockState;
import amp.awec.util.Vec3i;
import net.minecraft.core.world.World;

import java.util.HashMap;
import java.util.Map;

public class WorldChange {
	public Map<Vec3i, BlockState> changedBlocks = new HashMap<>();
	public int changedBlockCount = 0;

	public void putChange(Vec3i pos, BlockState blockState) {
		Vec3i newPos = new Vec3i(pos);
		if (changedBlocks.put(newPos, blockState) == null) {
			changedBlockCount++;
		}
	}

	public void update(WorldChange other) {
		this.changedBlocks.putAll(other.changedBlocks);
		this.changedBlockCount = this.changedBlocks.size();
	}

	public WorldChange apply(World world) {
		WorldChange oldWorld = new WorldChange();

		changedBlocks.forEach((pos, blockState) -> {
			BlockState oldBlock = blockState.setNotify(world, pos);
			oldWorld.putChange(pos, oldBlock);
		});

		return oldWorld;
	}

	@Override
	public String toString() {
		return "WorldChange(" + changedBlockCount + " changed blocks)";
	}
}
