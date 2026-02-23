package amp.awec.util;

import amp.awec.WorldEditMod;
import amp.awec.config.Config;
import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;

public class WandHelper {
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

}
