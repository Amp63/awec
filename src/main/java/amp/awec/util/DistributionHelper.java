package amp.awec.util;

import amp.awec.pattern.BlockMask;
import amp.awec.volume.CuboidVolume;
import amp.awec.volume.CuboidVolumeIterator;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;

import java.util.HashMap;
import java.util.Map;

public class DistributionHelper {
	public static Map<Block<?>, Long> getDistribution(World world, CuboidVolume volume, BlockMask mask) {
		Map<Block<?>, Long> distribution = new HashMap<>();

		CuboidVolumeIterator iterator = new CuboidVolumeIterator(volume);
		while (iterator.hasNext()) {
			BlockState blockState = new BlockState(world, iterator.next());
			if (mask.matches(blockState)) {
				Block<?> block = blockState.block;

				if (!distribution.containsKey(block)) {
					distribution.put(block, 1L);
				}
				else {
					long current = distribution.get(block);
					distribution.put(block, current + 1);
				}
			}
		}

		return distribution;
	}
}
