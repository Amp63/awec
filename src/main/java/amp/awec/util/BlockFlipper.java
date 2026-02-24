package amp.awec.util;

import amp.awec.WorldEditMod;
import net.minecraft.core.block.*;
import net.minecraft.core.block.piston.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


// TODO:
// sign
// trapdoor
// chest
public class BlockFlipper {
	public static Map<Class<?>, BiFunction<Integer, Vec3i, Integer>> flipMap = new HashMap<>();

	public static void initialize() {
		flipMap.put(BlockLogicStairs.class, BlockFlipper::stairs);
		flipMap.put(BlockLogicStairsPainted.class, BlockFlipper::stairs);
		flipMap.put(BlockLogicSlab.class, BlockFlipper::slab);
		flipMap.put(BlockLogicSlabPainted.class, BlockFlipper::slab);
		flipMap.put(BlockLogicButton.class, BlockFlipper::button);
		flipMap.put(BlockLogicButtonPainted.class, BlockFlipper::button);
		flipMap.put(BlockLogicPressurePlate.class, BlockFlipper::pressurePlate);
		flipMap.put(BlockLogicPressurePlatePainted.class, BlockFlipper::pressurePlate);
		flipMap.put(BlockLogicMotionSensor.class, BlockFlipper::sixDirectionBlock);
		flipMap.put(BlockLogicPistonBase.class, BlockFlipper::sixDirectionBlock);
		flipMap.put(BlockLogicPistonBaseSteel.class, BlockFlipper::sixDirectionBlock);
		flipMap.put(BlockLogicPistonBaseSticky.class, BlockFlipper::sixDirectionBlock);
		flipMap.put(BlockLogicPistonHead.class, BlockFlipper::sixDirectionBlock);
		flipMap.put(BlockLogicPistonMoving.class, BlockFlipper::sixDirectionBlock);
		flipMap.put(BlockLogicActivator.class, BlockFlipper::sixDirectionBlock);
		flipMap.put(BlockLogicFurnace.class, BlockFlipper::fourDirectionBlock);
		flipMap.put(BlockLogicFurnaceBlast.class, BlockFlipper::fourDirectionBlock);
		flipMap.put(BlockLogicTrommel.class, BlockFlipper::fourDirectionBlock);
		flipMap.put(BlockLogicDoor.class, BlockFlipper::door);
		flipMap.put(BlockLogicDoorPainted.class, BlockFlipper::door);
	}

	public static void flip(BlockState blockState, Vec3i flipVector) {
		Block<?> block = blockState.block;
		if (block == null) {
			return;
		}

		Class<?> logicClass = block.getLogic().getClass();
		if (flipMap.containsKey(logicClass)) {
			blockState.metadata = flipMap.get(logicClass).apply(blockState.metadata, flipVector);
		}
	}

	public static int stairs(int metadata, Vec3i flipVector) {
		metadata ^= (flipVector.x & ~(metadata >> 1 & 1));
		metadata ^= (flipVector.z & (metadata >> 1 & 1));
		metadata ^= (flipVector.y & 1) << 3;
		return metadata;
	}

	public static int slab(int metadata, Vec3i flipVector) {
		return metadata ^ (flipVector.y & 1) << 1;
	}

	public static int button(int metadata, Vec3i flipVector) {
		int metadataMod = metadata % 8;

		if (flipVector.x == 1) {
			if (metadataMod >= 1 && metadataMod <= 2) {
				return metadata ^ 1;
			}
		}
		if (flipVector.z == 1) {
			if (metadataMod >= 3 && metadataMod <= 4) {
				return metadata ^ 1;
			}
		}
		if (flipVector.y == 1) {
			if (metadataMod >= 5 && metadataMod <= 6) {
				return metadata ^ 1;
			}
		}

		return metadata;
	}

	public static int pressurePlate(int metadata, Vec3i flipVector) {
		if (flipVector.y == 1) {
			if (metadata >= 0 && metadata <= 3) {
				return metadata ^ 2;
			}
		}
		if (flipVector.z == 1) {
			if (metadata >= 4 && metadata <= 7) {
				return metadata ^ 2;
			}
		}
		if (flipVector.x == 1) {
			if (metadata >= 8 && metadata <= 11) {
				return metadata ^ 2;
			}
		}

		return metadata;
	}

	public static int fourDirectionBlock(int metadata, Vec3i flipVector) {
		if (flipVector.z == 1) {
			if (metadata >= 2 && metadata <= 3) {
				return metadata ^ 1;
			}
		}
		if (flipVector.x == 1) {
			if (metadata >= 4 && metadata <= 5) {
				return metadata ^ 1;
			}
		}

		return metadata;
	}

	public static int sixDirectionBlock(int metadata, Vec3i flipVector) {
		if (flipVector.y == 1) {
			if (metadata < 3) {
				return metadata ^ 1;
			}
		}
		if (flipVector.z == 1) {
			if (metadata >= 2 && metadata <= 3) {
				return metadata ^ 1;
			}
		}
		if (flipVector.x == 1) {
			if (metadata >= 4 && metadata <= 5) {
				return metadata ^ 1;
			}
		}

		return metadata;
	}

	public static int sign(int metadata, Vec3i flipVector) {
		// TODO
		return metadata;
	}

	public static int door(int metadata, Vec3i flipVector) {
		if (metadata % 2 == 0 && flipVector.x == 1) {
			return metadata ^ 2;
		}
		if (metadata % 2 == 1 && flipVector.z == 1) {
			return metadata ^ 2;
		}
		return metadata;
	}
}
