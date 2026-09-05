package amp.awec.pattern;

import amp.awec.util.BlockState;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class BlockTypes {
	public static final String BLOCK_TYPES_DATA_PATH = "/static/block-types.json";
	public static final Map<String, Set<Block<?>>> blockTypeMap = new HashMap<>();

	static {
		try (InputStream is = BlockTypes.class.getResourceAsStream(BLOCK_TYPES_DATA_PATH)) {
			assert is != null;
			try (Reader reader = new InputStreamReader(is)) {
				Map<String, List<String>> unparsedTypes = new Gson().fromJson(reader, new TypeToken<Map<String, List<String>>>(){}.getType());

				for (Map.Entry<String, List<String>> entry : unparsedTypes.entrySet()) {
					Set<Block<?>> blockSet = new HashSet<>();

					for (String blockNamespace : entry.getValue()) {
						NamespaceID namespaceID = NamespaceID.getTemp(blockNamespace);
						Block<?> block = Blocks.blockMap.get(namespaceID);
						blockSet.add(block);
					}

					blockTypeMap.put(entry.getKey(), blockSet);
				}
			}
		}
		catch (IOException | HardIllegalArgumentException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}
