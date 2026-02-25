package amp.awec.command.argtypes;

import amp.awec.pattern.BlockAliases;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestionHelper {
	public static List<String> getBlockSuggestions(String partialString) {
		List<String> suggestions = Arrays.stream(Blocks.blocksList)
			.map(b -> b != null ? b.namespaceId().value() : "air")
			.map(b -> b.replaceAll("block/", ""))
			.collect(Collectors.toList());

		suggestions.addAll(BlockAliases.aliasMap.keySet());

		return suggestions.stream()
			.filter(s -> s.startsWith(partialString))
			.collect(Collectors.toList());
	}

	public static List<String> getBlockTagSuggestions(String partialString) {
		return BlockTags.TAG_LIST.stream()
			.map(Tag::getName)
			.filter(name -> name.startsWith(partialString))
			.map(name -> "#" + name)
			.collect(Collectors.toList());
	}
}
