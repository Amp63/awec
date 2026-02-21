package amp.awec;

import amp.awec.command.navigation.CommandAscend;
import amp.awec.command.navigation.CommandDescend;
import amp.awec.command.navigation.CommandThru;
import amp.awec.command.navigation.CommandUp;
import amp.awec.command.operation.*;
import amp.awec.command.operation.CommandShift;
import amp.awec.command.permission.CommandWorldEditWhitelist;
import amp.awec.command.selection.CommandPos1;
import amp.awec.command.selection.CommandPos2;
import amp.awec.command.selection.CommandToggleWand;
import amp.awec.command.selection.CommandWand;
import amp.awec.command.undoredo.CommandRedo;
import amp.awec.command.undoredo.CommandUndo;
import amp.awec.permission.WorldEditWhitelist;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
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
		CommandManager.registerCommand(new CommandUp());
		CommandManager.registerCommand(new CommandThru());
		CommandManager.registerCommand(new CommandAscend());
		CommandManager.registerCommand(new CommandDescend());
		CommandManager.registerCommand(new CommandToggleWand());
		CommandManager.registerCommand(new CommandCopy());
		CommandManager.registerCommand(new CommandPaste());
		CommandManager.registerCommand(new CommandReplace());
		CommandManager.registerCommand(new CommandWalls());
		CommandManager.registerCommand(new CommandStack());
		CommandManager.registerCommand(new CommandUndo());
		CommandManager.registerCommand(new CommandRedo());
		CommandManager.registerCommand(new CommandMove());
		CommandManager.registerCommand(new CommandShift());

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
