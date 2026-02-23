package amp.awec.util;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;

public class MessageHelper {
	private static final String INFO_TAG = "§8[§1ℹ§8] ";
	private static final String ERROR_TAG = "§8[§e❌§8] ";
	private static final String SUCCESS_TAG = "§8[§5✔§8] ";

	private static final String ERROR_SOUND = "note.bd";

	public static void info(CommandSource source, String message) {
		if (source.getSender() != null) {
			info(source.getSender(), message);
		}
		else {
			source.sendMessage(message);
		}
	}

	public static void info(Player player, String message) {
		player.sendMessage(INFO_TAG + TextFormatting.WHITE + message + TextFormatting.RESET);
	}

	public static void error(CommandSource source, String message) {
		if (source.getSender() != null) {
			error(source.getSender(), message);
		}
		else {
			source.sendMessage(message);
		}
	}

	public static void error(Player player, String message) {
		player.sendMessage(ERROR_TAG + TextFormatting.RED + message + TextFormatting.RESET);
		if (player.world != null) {
			player.world.playSoundAtEntity(null, player, ERROR_SOUND, 1.0f, 1.0f);
		}
	}

	public static void success(CommandSource source, String message) {
		if (source.getSender() != null) {
			success(source.getSender(), message);
		}
		else {
			source.sendMessage(message);
		}
	}

	public static void success(Player player, String message) {
		player.sendMessage(SUCCESS_TAG + TextFormatting.WHITE + message + TextFormatting.RESET);
	}
}
