package amp.awec.command.schematic;

import amp.awec.WorldEditMod;
import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeSchematicPath;
import amp.awec.schematic.Schematic;
import amp.awec.schematic.SchematicsManager;
import amp.awec.util.Vec3i;
import amp.awec.volume.CuboidVolumeBuffer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

import java.io.FileNotFoundException;
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
								source.sendMessage("Clipboard is empty");
								return 0;
							}

							String filePath = context.getArgument("file_path", String.class);

							Schematic schem = Schematic.fromVolumeBuffer(playerData.data.clipboardBuffer, playerData.data.copyOffset);
							try {
								String wrotePath = SchematicsManager.create(schem, filePath);
								source.sendMessage("Wrote schematic to \"" + wrotePath + "\"");
								return 1;
							}
							catch (SecurityException e) {
								source.sendMessage(e.getMessage());
							}
							catch (IOException e) {
								WorldEditMod.LOGGER.error(e.getMessage());
								source.sendMessage("Got error while writing schematic");
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

								source.sendMessage("Schematic loaded successfully");
								return 1;
							}
							catch (NoSuchFileException e) {
								WorldEditMod.LOGGER.error(e.toString());
								source.sendMessage("Could not find schematic \"" + filePath + "\"");
							}
							catch (SecurityException e) {
								source.sendMessage(e.getMessage());
							}
							catch (IOException e) {
								WorldEditMod.LOGGER.error(e.toString());
								source.sendMessage("Got error while loading schematic");
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

								source.sendMessage("Schematic deleted");
								return 1;
							}
							catch (NoSuchFileException e) {
								WorldEditMod.LOGGER.error(e.toString());
								source.sendMessage("Could not find schematic \"" + filePath + "\"");
							}
							catch (SecurityException e) {
								source.sendMessage(e.getMessage());
							}
							catch (IOException e) {
								WorldEditMod.LOGGER.error(e.toString());
								source.sendMessage("Got error while deleting schematic");
							}

							return 0;
						})
					)
				)
		);
	}
}
