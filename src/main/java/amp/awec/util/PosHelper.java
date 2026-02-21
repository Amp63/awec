package amp.awec.util;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PosHelper {
	public static Vec3i getPlayerBlockPos(Player player) {
		Vec3 playerPos = player.getPosition(1.0f, true);
		return new Vec3i(
			(int) Math.floor(playerPos.x),
			(int) Math.floor(playerPos.y)-2,
			(int) Math.floor(playerPos.z)
		);
	}
}
