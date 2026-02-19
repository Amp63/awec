package amp.awec.mixin;

import amp.awec.util.Vec3i;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import amp.awec.permissions.WorldEditPermissions;
import amp.awec.util.WandHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Environment(EnvType.CLIENT)
@Mixin(value = PlayerController.class, remap = false)
public class WandPos1ClientMixin {
	@Shadow
	@Final
	protected Minecraft mc;

	@Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void startDestroyBlock(int x, int y, int z, Side side, double xHit, double yHit, boolean repeat, CallbackInfo ci) {
		Player player = mc.thePlayer;

		if (!WorldEditPermissions.canUseWorldEdit(player) || !WandHelper.isHoldingWand(player)) {
			return;
		}

		PlayerData playerData = WorldEditMod.getPlayerData(player);
		if (playerData == null) {
			return;
		}

		Vec3i pos = new Vec3i(x, y, z);
		playerData.selection.setCorner1(pos);
		player.sendMessage("Corner 1" + " set to " + pos);
		ci.cancel();
	}
}
