package amp.awec.pattern;

import amp.awec.WorldEditMod;
import amp.awec.util.BlockState;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class BlockAliases {
	public static final String ALIASES_DATA_PATH = "/static/block-aliases.json";
	public static final Map<String, BlockState> aliasMap = new HashMap<>();

	public static void initialize() {
		try (InputStream is = BlockAliases.class.getResourceAsStream(ALIASES_DATA_PATH)) {
			assert is != null;
			try (Reader reader = new InputStreamReader(is)) {
				Map<String, String> unparsedAliases = new Gson().fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
				parseAliases(unparsedAliases);
			}
		}
		catch (IOException | HardIllegalArgumentException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static void parseAliases(Map<String, String> unparsedAliases) throws HardIllegalArgumentException {
		for (Map.Entry<String, String> entry : unparsedAliases.entrySet()) {
			Block<?> block;
			int metadata = 0;

			String entryValue = entry.getValue();
			if (entryValue == null) {
				block = null;
			}
			else {
				String[] namespaceAndMetadata = entryValue.split(";");
				NamespaceID namespaceID = NamespaceID.getTemp(namespaceAndMetadata[0]);
				block = Blocks.blockMap.get(namespaceID);

				if (namespaceAndMetadata.length > 1) {
					metadata = Integer.parseInt(namespaceAndMetadata[1]);
				}
			}

			BlockState blockState = new BlockState(block, metadata);
			aliasMap.put(entry.getKey(), blockState);
		}
	}
}
