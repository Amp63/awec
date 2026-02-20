package amp.awec.util;

import net.minecraft.core.util.helper.Direction;

public class DirectionHelper {

	public static Direction getMajorDirection(double pitch, double yaw) {
		if (Math.abs(pitch) > 45) {
			return Direction.getVerticalDirection(pitch);
		}
		return Direction.getHorizontalDirection(yaw);
	}
}
