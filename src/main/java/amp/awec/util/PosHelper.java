package amp.awec.util;

import amp.awec.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.phys.Vec3;

public class PosHelper {
	public static BlockPos getPlayerBlockPos(Player player) {
		Vec3 playerPos = player.getPosition(1.0f, true);
		return new BlockPos(
			(int) Math.floor(playerPos.x),
			(int) Math.floor(playerPos.y)-2,
			(int) Math.floor(playerPos.z)
		);
	}
}
