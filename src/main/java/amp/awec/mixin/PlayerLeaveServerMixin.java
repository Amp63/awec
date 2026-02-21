package amp.awec.mixin;

import amp.awec.WorldEditMod;
import amp.awec.data.PlayerDataManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerList;
import net.minecraft.server.player.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(value = PlayerList.class, remap = false)
public class PlayerLeaveServerMixin {
	@Inject(method = "playerLoggedOut", at = @At("TAIL"))
	private void playerLoggedOut(PlayerServer entityplayermp, CallbackInfo ci) {
		PlayerDataManager.deletePlayerData(entityplayermp.uuid);
		WorldEditMod.LOGGER.info("Removed player data for " + entityplayermp.username);
	}
}
