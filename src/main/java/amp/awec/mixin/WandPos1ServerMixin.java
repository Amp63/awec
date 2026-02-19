package amp.awec.mixin;

import amp.awec.util.Vec3i;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
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
public class WandPos1ServerMixin {
	@Shadow
	public Player player;

	@Inject(method = "startMining", at = @At("HEAD"), cancellable = true)
	private void startMining(int x, int y, int z, Side side, CallbackInfo ci) {
		if (!WorldEditPermissions.canUseWorldEdit(player) || !WandHelper.isHoldingWand(player)) {
			return;
		}

		PlayerData playerData = WorldEditMod.getPlayerData(player);
		if (playerData == null) {
			return;
		}

		Vec3i pos = new Vec3i(x, y, z);
		playerData.selection.setCorner1(pos);
		player.sendMessage("Corner 1 set to " + pos);
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
