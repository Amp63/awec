package amp.awec.schematic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class SchematicsManager {
	private static final String SCHEMATICS_DIRECTORY = "schematics";

	public static String create(Schematic schem, String filePath) throws IOException, SecurityException {
		Path writePath = resolvePath(filePath);
		schem.writeToFile(writePath.toString());
		return writePath.toString();
	}

	public static Schematic load(String filePath) throws IOException, SecurityException {
		Path loadPath = resolvePath(filePath);
		return new Schematic(loadPath.toString());
	}

	public static void delete(String filePath) throws IOException, SecurityException {
		Path deletePath = resolvePath(filePath);
		Files.delete(deletePath);
	}

	public static String[] getAllFilePaths() throws IOException {
		Path dir = Paths.get(SCHEMATICS_DIRECTORY);
		try (Stream<Path> stream = Files.walk(dir)) {
			return stream
				.filter(Files::isRegularFile)
				.map(p -> dir.relativize(p).toString())
				.toArray(String[]::new);
		}
	}

	private static Path resolvePath(String path) throws IOException, SecurityException {
		if (!path.endsWith(".schem")) {
			path += ".schem";
		}

		Path schemDir = Paths.get(SCHEMATICS_DIRECTORY).toAbsolutePath();
		Path filePath = Paths.get(path);
		Path fullFilePath = schemDir.resolve(filePath).normalize();

		if (!fullFilePath.startsWith(schemDir)) {
			throw new SecurityException("Path escapes base directory");
		}

		Files.createDirectories(fullFilePath.getParent());
		return fullFilePath;
	}
}
