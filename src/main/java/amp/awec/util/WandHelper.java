package amp.awec.util;

import amp.awec.WorldEditMod;
import amp.awec.config.Config;
import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;

public class WandHelper {
	private static final double TARGET_POS_AIR_DISTANCE = 2;

	public static boolean isHoldingWand(Player player) {
		ItemStack heldItem = player.getHeldItem();
		PlayerData playerData = PlayerDataManager.getPlayerData(player.uuid);

		Item wandItem = getWandItem();

		return playerData != null && playerData.wandEnabled &&
			   heldItem != null && heldItem.itemID == wandItem.id;
	}

	public static Item getWandItem() {
		Integer wandItemId = Item.nameToIdMap.get(Config.WAND_ITEM);

		if (wandItemId == null) {
			return Items.TOOL_AXE_WOOD;
		}

		return Item.getItem(wandItemId);
	}

	public static Vec3i getTargetedPos(Player player, float partialTick) {
		float reach = player.gamemode.getBlockReachDistance();
		HitResult result = player.rayTrace(reach, partialTick, false, false);
		if (result != null) {
			return new Vec3i(result.x, result.y, result.z);
		}

		Vec3 playerPos = player.getPosition(partialTick, true);
		Vec3 shiftVector = player.getViewVector(partialTick).scale(TARGET_POS_AIR_DISTANCE);
		Vec3 targetedPos = playerPos.add(shiftVector.x, shiftVector.y, shiftVector.z);

		return new Vec3i(targetedPos);
	}

}
