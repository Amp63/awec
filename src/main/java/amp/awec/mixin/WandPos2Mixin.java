package amp.awec.mixin;

import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.MessageHelper;
import amp.awec.util.Vec3i;
import amp.awec.util.WandHelper;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class)
public class WandPos2Mixin {
	@Inject(method = "useItemRightClick", at = @At("HEAD"))
	private void useItemRightClick(World world, Player entityplayer, CallbackInfoReturnable<ItemStack> cir) {
		if (!WorldEditPermissions.canUseWorldEdit(entityplayer) || !WandHelper.isHoldingWand(entityplayer) || entityplayer.world == null) {
			return;
		}

		PlayerData playerData = PlayerDataManager.getPlayerData(entityplayer.uuid);

		Vec3i pos = WandHelper.getTargetedPos(entityplayer, 1.0f);

		playerData.getSelection(world).setCorner2(pos);
		MessageHelper.info(entityplayer, "Corner 2 set to " + pos);
	}
}
