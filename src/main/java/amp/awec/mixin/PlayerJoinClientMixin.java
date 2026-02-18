package amp.awec.mixin;

import amp.awec.WorldEditMod;
import amp.awec.data.PlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = World.class, remap = false)
public class PlayerJoinClientMixin {

	@Inject(method = "spawnPlayerWithLoadedChunks", at = @At("TAIL"))
	private void spawnPlayerWithLoadedChunks(Player player, CallbackInfo ci) {
		WorldEditMod.LOGGER.info("Created player data for " + player.uuid);
		WorldEditMod.PLAYER_DATA.put(player.uuid, new PlayerData());
	}
}
