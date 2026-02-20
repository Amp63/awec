package amp.awec.mixin;

import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
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
public class PlayerJoinServerMixin {

	@Inject(method = "addPlayer", at = @At("TAIL"))
	private void addPlayer(PlayerServer player, CallbackInfo ci) {
		WorldEditMod.createPlayerData(player);
		WorldEditMod.LOGGER.info("Created player data for " + player.username);
	}
}
