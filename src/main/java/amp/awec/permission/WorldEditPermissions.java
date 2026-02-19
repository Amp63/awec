package amp.awec.permission;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;

public class WorldEditPermissions {
	public static boolean canUseWorldEdit(CommandSource source) {
		Player player = source.getSender();
		return canUseWorldEdit(player);
	}

	public static boolean canUseWorldEdit(Player player) {
		if (player == null) {
			return false;
		}

		// Client -- Allow if cheats are enabled
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			World world = player.world;
			if (world == null) {
				return false;
			}
			return player.world.getLevelData().getCheatsEnabled();
		}
		// Server -- Allow if operator or the player is on the worldedit whitelist
		else {
			MinecraftServer server = ((PlayerServer) player).mcServer;
			boolean isOp = server.playerList.isOp(player.uuid);
			return isOp || WorldEditWhitelist.isWhitelisted(player.uuid);
		}
	}
}
