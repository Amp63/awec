package amp.awec.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	public static int lastIndexOfSet(String input, String regex) {
		Matcher matcher = Pattern.compile(regex).matcher(input);
		int lastIndex = -1;
		while (matcher.find()) {
			lastIndex = matcher.start();
		}
		return lastIndex;
	}
}
