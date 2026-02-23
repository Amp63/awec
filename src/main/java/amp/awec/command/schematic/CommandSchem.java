package amp.awec.command.schematic;

import amp.awec.WorldEditMod;
import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeSchematicPath;
import amp.awec.schematic.Schematic;
import amp.awec.schematic.SchematicsManager;
import amp.awec.util.MessageHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class CommandSchem implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/schem")
				.requires(source -> ((CommandSource)source).hasAdmin())
				.then(ArgumentBuilderLiteral.literal("save")
					.then(ArgumentBuilderRequired.argument("file_path", ArgumentTypeString.string())
						.executes(context -> {
							CommandSource source = (CommandSource) context.getSource();
							CommandPlayerData playerData = CommandPlayerData.get(source, false);
							if (playerData == null) {
								return 0;
							}

							if (playerData.data.clipboardBuffer == null) {
								MessageHelper.error(source, "Clipboard is empty");
								return 0;
							}

							String filePath = context.getArgument("file_path", String.class);

							Schematic schem = Schematic.fromVolumeBuffer(
								playerData.data.clipboardBuffer, playerData.data.copyOffset,
								filePath, playerData.player.username, System.currentTimeMillis()
							);

							try {
								String wrotePath = SchematicsManager.create(schem, filePath);
								MessageHelper.success(source, "Wrote schematic to \"" + wrotePath + "\"");
								return 1;
							}
							catch (SecurityException e) {
								MessageHelper.error(source, e.getMessage());
							}
							catch (IOException e) {
								WorldEditMod.LOGGER.error(e.getMessage());
								MessageHelper.error(source, "Got error while writing schematic");
							}

							return 0;
						})
					)
				)
				.then(ArgumentBuilderLiteral.literal("load")
					.then(ArgumentBuilderRequired.argument("file_path", ArgumentTypeSchematicPath.path())
						.executes(context -> {
							CommandSource source = (CommandSource) context.getSource();
							CommandPlayerData playerData = CommandPlayerData.get(source, false);
							if (playerData == null) {
								return 0;
							}

							String filePath = context.getArgument("file_path", String.class);
							try {
								Schematic schem = SchematicsManager.load(filePath);
								Schematic.LoadResult result = schem.toVolumeBuffer();
								playerData.data.clipboardBuffer = result.buffer;
								playerData.data.copyOffset = result.offset;

								MessageHelper.success(source, "Schematic loaded successfully");
								return 1;
							}
							catch (NoSuchFileException e) {
								WorldEditMod.LOGGER.error(e.toString());
								MessageHelper.error(source,"Could not find schematic \"" + filePath + "\"");
							}
							catch (SecurityException e) {
								MessageHelper.error(source, e.getMessage());
							}
							catch (Schematic.ModNotFoundException e) {
								WorldEditMod.LOGGER.error(e.toString());
								MessageHelper.error(source, e.getMessage());
							}
							catch (Exception e) {
								WorldEditMod.LOGGER.error(e.toString());
								MessageHelper.error(source, "Got error while loading schematic");
							}

							return 0;
						})
					)
				)
				.then(ArgumentBuilderLiteral.literal("delete")
					.then(ArgumentBuilderRequired.argument("file_path", ArgumentTypeSchematicPath.path())
						.executes(context -> {
							CommandSource source = (CommandSource) context.getSource();
							CommandPlayerData playerData = CommandPlayerData.get(source, false);
							if (playerData == null) {
								return 0;
							}

							String filePath = context.getArgument("file_path", String.class);
							try {
								SchematicsManager.delete(filePath);

								MessageHelper.success(source, "Schematic deleted");
								return 1;
							}
							catch (NoSuchFileException e) {
								WorldEditMod.LOGGER.error(e.toString());
								MessageHelper.error(source, "Could not find schematic \"" + filePath + "\"");
							}
							catch (SecurityException e) {
								MessageHelper.error(source, e.getMessage());
							}
							catch (IOException e) {
								WorldEditMod.LOGGER.error(e.toString());
								MessageHelper.error(source,"Got error while deleting schematic");
							}

							return 0;
						})
					)
				)
		);
	}
}
