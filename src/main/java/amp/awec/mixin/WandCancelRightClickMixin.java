package amp.awec.mixin;

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

@Mixin(value = Block.class)
public class WandCancelRightClickMixin {

	@Inject(method = "onBlockRightClicked", at = @At("HEAD"), cancellable = true)
	private void onRightClickBlock(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit, CallbackInfoReturnable<Boolean> cir) {
		if (!WorldEditPermissions.canUseWorldEdit(player) || !WandHelper.isHoldingWand(player) || player.world == null) {
			return;
		}

		cir.cancel();
	}
}
