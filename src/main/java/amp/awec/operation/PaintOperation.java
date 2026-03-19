package amp.awec.operation;

import amp.awec.WorldEditMod;
import amp.awec.command.CommandPlayerData;
import amp.awec.pattern.BlockMask;
import amp.awec.pattern.BlockPattern;
import amp.awec.util.BlockState;
import amp.awec.util.MessageHelper;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolume;
import amp.awec.volume.CuboidVolumeIterator;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.IPaintable;
import net.minecraft.core.block.IPainted;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public class PaintOperation {
	public static WorldChange execute(World world, CuboidVolume volume, DyeColor dyeColor, BlockMask mask) {
		WorldChange result = new WorldChange();

		CuboidVolumeIterator iterator = new CuboidVolumeIterator(volume);

		while (iterator.hasNext()) {
			Vec3i pos = iterator.next();
			BlockState blockState = new BlockState(world, pos);
			if (!mask.matches(blockState)) {
				continue;
			}

			if (blockState.block == null) {
				continue;
			}

			if (!Block.hasLogicClass(blockState.block, IPaintable.class)) {
				continue;
			}

			IPaintable paintable = (IPaintable) blockState.block.getLogic();
			if (paintable.canBePainted()) {
				paintable.setColor(world, pos.x, pos.y, pos.z, dyeColor);
				result.putChange(pos, blockState);
			}
		}

		return result;
	}
}
