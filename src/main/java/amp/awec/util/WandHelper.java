package amp.awec.util;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;

public class WandHelper {
	public static final Item WAND_ITEM = Items.TOOL_AXE_WOOD;

	public static boolean isHoldingWand(Player player) {
		ItemStack heldItem = player.getHeldItem();
		return heldItem != null && heldItem.itemID == WAND_ITEM.id;
	}

}
