package amp.awec.util;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.pos.TilePos;
import org.joml.RoundingMode;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;

public class PosHelper {
	public static Vector3d getPlayerBlockPos(Player player) {
		Vector3dc playerPos = player.getPosition(1.0f, false);
		return new Vector3d(
			(int) Math.floor(playerPos.x()),
			(int) Math.floor(playerPos.y()),
			(int) Math.floor(playerPos.z())
		);
	}

	public static Vector3i getPlayerBlockPosFloor(Player player) {
		return new Vector3i(getPlayerBlockPos(player), RoundingMode.FLOOR);
	}

	public static TilePos getPlayerTilePos(Player player) {
		return new TilePos(getPlayerBlockPosFloor(player));
	}
}
