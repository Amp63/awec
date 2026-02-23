package amp.awec;

import amp.awec.command.config.CommandReloadConfig;
import amp.awec.command.navigation.CommandAscend;
import amp.awec.command.navigation.CommandDescend;
import amp.awec.command.navigation.CommandThru;
import amp.awec.command.navigation.CommandUp;
import amp.awec.command.operation.*;
import amp.awec.command.permission.CommandWorldEditWhitelist;
import amp.awec.command.schematic.CommandSchem;
import amp.awec.command.selection.*;
import amp.awec.command.undoredo.CommandRedo;
import amp.awec.command.undoredo.CommandUndo;
import amp.awec.config.Config;
import amp.awec.permission.WorldEditWhitelist;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.net.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WorldEditMod implements ModInitializer {
	public static final String MOD_ID = "awec";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		try {
			Config.load();
			LOGGER.info("Configuration loaded successfully");
		}
		catch (IOException e) {
			LOGGER.error("Failed to load configuration");
			throw new RuntimeException("Failed to load WorldEdit configuration");
		}

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
		CommandManager.registerCommand(new CommandExpand());
		CommandManager.registerCommand(new CommandSphere());
		CommandManager.registerCommand(new CommandHollowSphere());
		CommandManager.registerCommand(new CommandCylinder());
		CommandManager.registerCommand(new CommandHollowCylinder());
		CommandManager.registerCommand(new CommandSchem());
		CommandManager.registerCommand(new CommandReloadConfig());


		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			WorldEditWhitelist.initialize();
			CommandManager.registerServerCommand(new CommandWorldEditWhitelist());
		}
		else {
			CommandManager.registerCommand(new CommandDrawSel());
		}

		LOGGER.info("WorldEdit initialized.");
	}
}
