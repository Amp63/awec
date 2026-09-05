package amp.awec.mixin;

import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.MessageHelper;
import amp.awec.util.WandHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.server.entity.player.PlayerServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(value = PlayerServer.class)
public class WandPos1ServerMixin {

	// Inject inside the if statement to prevent double calling when selecting a block
	@Inject(method = "swingItem", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/server/entity/player/PlayerServer;swingProgressInt:I",
		opcode = Opcodes.PUTFIELD
	))
	private void swingItem(CallbackInfo ci) {
		Player player = (Player) (Object) this;

		if (!WorldEditPermissions.canUseWorldEdit(player) || !WandHelper.isHoldingWand(player)) {
			return;
		}

		PlayerData playerData = PlayerDataManager.getPlayerData(player.uuid);

		TilePos pos = WandHelper.getTargetedPos(player, 1.0f);
		playerData.getSelection(player.world).setCorner1(pos);

		MessageHelper.info(player, "Corner 1" + " set to " + pos);
	}
}
