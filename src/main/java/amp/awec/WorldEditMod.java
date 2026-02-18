package amp.awec;

import amp.awec.command.*;
import amp.awec.data.PlayerData;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.command.CommandManager;
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

	@Override
	public void onInitialize() {
		CommandManager.registerCommand(new CommandPos1());
		CommandManager.registerCommand(new CommandPos2());
		CommandManager.registerCommand(new CommandWand());
		CommandManager.registerCommand(new CommandSet());
		CommandManager.registerCommand(new CommandUp());
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
