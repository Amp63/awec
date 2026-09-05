package amp.awec.util;

import amp.awec.WorldEditMod;
import net.minecraft.core.util.helper.Direction;
import org.joml.Vector3i;

public class DirectionHelper {

	// TODO: fix ts
	public static Direction getMajorDirection(double pitch, double yaw) {
		if (Math.abs(pitch) > 45) {
			if (pitch < 0) return Direction.UP;
			return Direction.DOWN;
		}
		int yawDir = Math.floorMod((int) Math.floor((yaw + 45) / 90), 4);
		return switch (yawDir) {
			case 0 -> Direction.SOUTH;
			case 1 -> Direction.WEST;
			case 2 -> Direction.NORTH;
			default -> Direction.EAST;
		};
	}

	public static Vector3i getVec3i(Direction direction) {
		Vector3i vec = new Vector3i(0, 0, 0);
		direction.getOffset(vec);
		return vec;
	}
}
