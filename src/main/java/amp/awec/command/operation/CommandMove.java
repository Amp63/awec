package amp.awec.command.operation;

import amp.awec.command.CommandPlayerData;
import amp.awec.command.argtypes.ArgumentTypeDirection;
import amp.awec.operation.MoveOperation;
import amp.awec.operation.WorldChange;
import amp.awec.permission.WorldEditPermissions;
import amp.awec.util.DirectionHelper;
import amp.awec.util.MessageHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.util.helper.Direction;

public class CommandMove implements CommandManager.CommandRegistry {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		CommandMoveBase.register(dispatcher, "/move", false);
	}
}
