package amp.awec.mixin;

import amp.awec.BlockPos;
import amp.awec.ModState;
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

import static amp.awec.util.WandHelper.isHoldingWand;

@Environment(EnvType.SERVER)
@Mixin(value = ServerPlayerController.class, remap = false)
public class WandPos1ServerMixin {
	@Shadow
	public Player player;

	@Inject(method = "startMining", at = @At("HEAD"), cancellable = true)
	private void startMining(int x, int y, int z, Side side, CallbackInfo ci) {
		if (isHoldingWand(player)) {
			ModState.corner1 = new BlockPos(x, y, z);
			player.sendMessage("Corner 1 set to " + ModState.corner1);
			ci.cancel();
			if (player.world != null) {
				int blockId = 0;
				Block<?> block = player.world.getBlock(x, y, z);
				if (block != null) {
					blockId = block.id();
				}
				player.world.notifyBlockChange(x, y, z, blockId);
			}
		}
	}
}
