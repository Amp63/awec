package amp.awec;

import amp.awec.command.*;
import amp.awec.data.PlayerData;
import amp.awec.permissions.WorldEditWhitelist;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldEditMod implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
	public static final String MOD_ID = "awec";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

	public static @Nullable PlayerData getPlayerData(Player player) {
		return PLAYER_DATA.get(player.uuid);
	}

	@Override
	public void onInitialize() {
		CommandManager.registerCommand(new CommandPos1());
		CommandManager.registerCommand(new CommandPos2());
		CommandManager.registerCommand(new CommandWand());
		CommandManager.registerCommand(new CommandSet());
		CommandManager.registerCommand(new CommandUp());

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			WorldEditWhitelist.initialize();
			CommandManager.registerServerCommand(new CommandWorldEditWhitelist());
		}
		LOGGER.info("WorldEdit initialized.");
	}

	@Override
	public void onRecipesReady() {}

	@Override
	public void initNamespaces() {}

	@Override
	public void beforeGameStart() {}

	@Override
	public void afterGameStart() {}
}
