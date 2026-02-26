package amp.awec.mixin;

import amp.awec.data.PlayerDataManager;
import amp.awec.util.MessageHelper;
import amp.awec.util.Vec3i;
import amp.awec.data.PlayerData;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.WandHelper;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Block.class, remap = false)
public class WandPos2Mixin {

	@Inject(method = "onBlockRightClicked", at = @At("HEAD"), cancellable = true)
	private void onRightClickBlock(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit, CallbackInfoReturnable<Boolean> cir) {
		if (!WorldEditPermissions.canUseWorldEdit(player) || !WandHelper.isHoldingWand(player) || player.world == null) {
			return;
		}

		PlayerData playerData = PlayerDataManager.getPlayerData(player.uuid);

		Vec3i pos = new Vec3i(x, y, z);
		playerData.getSelection(world).setCorner2(pos);
		MessageHelper.info(player, "Corner 2 set to " + pos);

		cir.cancel();
	}
}
