package amp.awec.mixin;

import amp.awec.WorldEditMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.player.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(value = PlayerManager.class, remap = false)
public class PlayerLeaveServerMixin {
	@Inject(method = "removePlayer", at = @At("TAIL"))
	private void removePlayer(PlayerServer entityplayermp, CallbackInfo ci) {
		WorldEditMod.LOGGER.info("Removed player data for " + entityplayermp.uuid);
		WorldEditMod.PLAYER_DATA.remove(entityplayermp.uuid);
	}
}
