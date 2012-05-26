package shared;

public class Util {

	public static final String[] getArgs(String s, int args) {
		String[] temp = s.split(" ");
		String[] rVal = new String[args];

		for (int i = 0; i < args; i++) {
			rVal[i] = temp[i];
		}

		rVal[args - 1] = s.substring(s.lastIndexOf(temp[args - 1]));

		return rVal;
	}

	public static boolean checkArgs(String s, int args) {
		return s.split(" ").length >= args;
	}

	public static int maxArgs(String s) {
		return s.split(" ").length;
	}

	public static String formatChannel(String s) {
		return (s.startsWith("#") || s.startsWith("&")) ? s : "#" + s;
	}

}
