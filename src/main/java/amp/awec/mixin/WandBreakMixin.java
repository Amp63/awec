package amp.awec.mixin;

import amp.awec.BlockPos;
import amp.awec.ModState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static amp.awec.util.WandHelper.isHoldingWand;

@Mixin(value = PlayerController.class, remap = false)
public class WandBreakMixin {
	@Shadow
	@Final
	protected Minecraft mc;

	@Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void startDestroyBlock(int x, int y, int z, Side side, double xHit, double yHit, boolean repeat, CallbackInfo ci) {
		Player player = mc.thePlayer;
		if (isHoldingWand(player)) {
			ModState.corner1 = new BlockPos(x, y, z);
			player.sendMessage("Corner 1" + " set to " + ModState.corner1);
			ci.cancel();
		}
	}
}
