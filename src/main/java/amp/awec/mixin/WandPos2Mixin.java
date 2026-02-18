package amp.awec.mixin;

import amp.awec.BlockPos;
import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static amp.awec.util.WandHelper.isHoldingWand;

@Mixin(value = Block.class, remap = false)
public class WandPos2Mixin {

	@Inject(method = "onBlockRightClicked", at = @At("HEAD"))
	private void onRightClickBlock(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit, CallbackInfoReturnable<Boolean> cir) {
		if (!isHoldingWand(player)) {
			return;
		}

		PlayerData playerData = WorldEditMod.getPlayerData(player);
		if (playerData == null) {
			return;
		}

		playerData.corner2 = new BlockPos(x, y, z);
		player.sendMessage("Corner 2 set to " + playerData.corner2);
	}
}
