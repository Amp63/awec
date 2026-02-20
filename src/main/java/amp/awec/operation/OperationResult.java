package amp.awec.operation;

import amp.awec.volume.CuboidVolume;
import amp.awec.util.Vec3i;
import amp.awec.volume.CopiedVolume;
import net.minecraft.core.world.World;

public class OperationResult {
	public int changedBlocks = 0;
	public CopiedVolume previousVolume;
	public Vec3i previousVolumePos;

	public void copyPreviousVolume(World world, CuboidVolume volume) {
		previousVolume = new CopiedVolume(world, volume);
		previousVolumePos = new Vec3i(volume.getMinCorner());
	}

	@Override
	public String toString() {
		return "OpResult(pos: [" + previousVolumePos + "])";
	}
}
