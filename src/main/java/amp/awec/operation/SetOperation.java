package amp.awec.operation;

import amp.awec.pattern.BlockMask;
import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockState;
import amp.awec.volume.CuboidVolume;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolumeIterator;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class SetOperation {

	public static WorldChange execute(World world, CuboidVolume volume, BlockPattern pattern) {
		return execute(world, volume, pattern, null);
	}

	public static WorldChange execute(World world, CuboidVolume volume, BlockPattern pattern, @Nullable BlockMask mask) {
		WorldChange result = new WorldChange();

		CuboidVolumeIterator iterator = new CuboidVolumeIterator(volume);

		while (iterator.hasNext()) {
			Vec3i setPos = iterator.next();
			BlockState sampledBlock = pattern.sample();
			if (sampledBlock != null) {
				BlockState oldBlock = sampledBlock.setNotify(world, setPos, mask);
				result.putChange(setPos, oldBlock);
			}
		}

		return result;
	}
}
