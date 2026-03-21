package amp.awec.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

// Stores data for //help command
public class HelpList {
	private static final String COMMAND_HELP_DATA_PATH = "/static/command-help.json";

	public static class CommandHelpData {
		public String usage;
		public String description;
		public String[] examples;
		public boolean requiresSelection;

		public CommandHelpData(Map<String, String> data) {
			this.usage = data.get("usage");
			this.description = data.get("description");

			String examplesString = data.get("examples");
			if (examplesString == null) {
				this.examples = new String[]{};
			}
			else {
				this.examples = examplesString.split("\n");
			}

			this.requiresSelection = data.get("requires_selection").equals("true");
		}

		@Override
		public String toString() {
			return String.format("CommandHelpData(usage: '%s', description: '%s')", this.usage, this.description);
		}
	}

	public static final Map<String, CommandHelpData> commandMap = new HashMap<>();

	static {
		try (InputStream is = HelpList.class.getResourceAsStream(COMMAND_HELP_DATA_PATH)) {
			assert is != null;
			try (Reader reader = new InputStreamReader(is)) {
				// Parse json
				Type tokenType = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
				Map<String, Map<String, String>> unparsedEntries = new Gson().fromJson(reader, tokenType);

				// Set command map entries
				for (Map.Entry<String, Map<String, String>> entry : unparsedEntries.entrySet()) {
					commandMap.put(entry.getKey(), new CommandHelpData(entry.getValue()));
				}
			}
		}
		catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}
