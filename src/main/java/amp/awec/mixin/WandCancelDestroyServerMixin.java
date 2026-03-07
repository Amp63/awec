package amp.awec.mixin;

import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.WandHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.server.world.ServerPlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(value = ServerPlayerController.class, remap = false)
public class WandCancelDestroyServerMixin {
	@Shadow
	public Player player;

	@Inject(method = "startMining", at = @At("HEAD"), cancellable = true)
	private void startMining(int x, int y, int z, Side side, CallbackInfo ci) {
		if (!WorldEditPermissions.canUseWorldEdit(player) || !WandHelper.isHoldingWand(player) || player.world == null) {
			return;
		}

		ci.cancel();

		if (player.world == null) {
			return;
		}

		int blockId = 0;
		Block<?> block = player.world.getBlock(x, y, z);
		if (block != null) {
			blockId = block.id();
		}
		player.world.notifyBlockChange(x, y, z, blockId);
	}
}
