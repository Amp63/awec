package amp.awec.mixin;

import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.MessageHelper;
import amp.awec.util.WandHelper;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class)
public class WandPos2Mixin {
	@Inject(method = "useItem*", at = @At("HEAD"))
	private void useItem(World world, Player entityplayer, CallbackInfoReturnable<ItemStack> cir) {
		if (!WorldEditPermissions.canUseWorldEdit(entityplayer) || !WandHelper.isHoldingWand(entityplayer)) {
			return;
		}

		PlayerData playerData = PlayerDataManager.getPlayerData(entityplayer.uuid);

		TilePos pos = WandHelper.getTargetedPos(entityplayer, 1.0f);

		playerData.getSelection(world).setCorner2(pos);
		MessageHelper.info(entityplayer, "Corner 2 set to " + pos);
	}
}
