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
import net.minecraft.core.world.pos.TilePos;
import org.joml.RoundingMode;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;

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

	public static TilePos getTargetedPos(Player player, float partialTick) {
		double reach = player.gamemode.getBlockReachDistance();
		HitResult result = player.rayCast(reach, partialTick, false, false, false);
		if (result instanceof HitResult.Tile) {
			return new TilePos(((HitResult.Tile) result).tilePos);
		}

		Vector3d playerPos = new Vector3d(player.getPosition(partialTick, true));
		Vector3d shiftVector = new Vector3d(player.getViewVector(partialTick)).mul(TARGET_POS_AIR_DISTANCE);
		Vector3d targetedPos = playerPos.add(shiftVector);

		return new TilePos(targetedPos);
	}

}
