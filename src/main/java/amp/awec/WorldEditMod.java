package amp.awec;

import amp.awec.command.CommandPos1;
import amp.awec.command.CommandPos2;
import amp.awec.command.CommandSet;
import amp.awec.command.CommandWand;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.net.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class WorldEditMod implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
	public static final String MOD_ID = "awec";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize() {
		CommandManager.registerCommand(new CommandPos1());
		CommandManager.registerCommand(new CommandPos2());
		CommandManager.registerCommand(new CommandWand());
		CommandManager.registerCommand(new CommandSet());
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
