package amp.awec.command;
import amp.awec.BlockPos;
import amp.awec.BlockVolumeIterator;
import amp.awec.ModState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeBlock;
import net.minecraft.core.net.command.helpers.BlockInput;
import net.minecraft.core.world.World;

public class CommandSet implements CommandManager.CommandRegistry {

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("/set")
//				.requires(CommandSource::hasAdmin)
				.then(ArgumentBuilderRequired.argument("block", ArgumentTypeBlock.block())
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						BlockInput blockInput = (BlockInput) context.getArgument("block", BlockInput.class);
						World world = source.getWorld();
						doSet(world, ModState.corner1, ModState.corner2, blockInput.getBlockId());
						return 1;
					})
				));
	}

	private void doSet(World world, BlockPos corner1, BlockPos corner2, int blockId) {
		BlockVolumeIterator iterator = new BlockVolumeIterator(corner1, corner2);
		while (iterator.hasNext()) {
			BlockPos setPos = iterator.next();
			world.setBlockWithNotify(setPos.x, setPos.y, setPos.z, blockId);
		}
	}
}
