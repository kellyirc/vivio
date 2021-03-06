/*
 * @author Raymond Hammarling
 * @description This module allows for the translating to and from of morse code.
 * @basecmd morse
 * @category utility
 */
package commands;

import java.util.Map;
import java.util.TreeMap;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class MorseTranslateCommand.
 */
public class MorseTranslateCommand extends Command {

	/** The letter to morse. */
	private static Map<String, String> letterToMorse;

	/** The morse to letter. */
	private static Map<String, String> morseToLetter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		setName("MorseTranslator");
		setHelpText("Translate to and from Morse code! (\".\" for short, \"-\" for long)");
		addAlias("morse");
		setUsableInPM(true);

		if (letterToMorse == null)
			letterToMorse = new TreeMap<String, String>();

		letterToMorse.clear();

		letterToMorse.put(" ", "/");
		letterToMorse.put("a", ".-");
		letterToMorse.put("b", "-...");
		letterToMorse.put("c", "-.-.");
		letterToMorse.put("d", "-..");
		letterToMorse.put("e", ".");
		letterToMorse.put("f", "..-.");
		letterToMorse.put("g", "--.");
		letterToMorse.put("h", "....");
		letterToMorse.put("i", "..");
		letterToMorse.put("j", ".---");
		letterToMorse.put("k", "-.-");
		letterToMorse.put("l", ".-..");
		letterToMorse.put("m", "--");
		letterToMorse.put("n", "-.");
		letterToMorse.put("o", "---");
		letterToMorse.put("p", ".--.");
		letterToMorse.put("q", "--.-");
		letterToMorse.put("r", ".-.");
		letterToMorse.put("s", "...");
		letterToMorse.put("t", "-");
		letterToMorse.put("u", "..-");
		letterToMorse.put("v", "...-");
		letterToMorse.put("w", ".--");
		letterToMorse.put("x", "-..-");
		letterToMorse.put("y", "-.--");
		letterToMorse.put("z", "--..");

		letterToMorse.put("1", ".----");
		letterToMorse.put("2", "..---");
		letterToMorse.put("3", "...--");
		letterToMorse.put("4", "....-");
		letterToMorse.put("5", ".....");
		letterToMorse.put("6", "-....");
		letterToMorse.put("7", "--...");
		letterToMorse.put("8", "---..");
		letterToMorse.put("9", "----.");
		letterToMorse.put("0", "-----");

		letterToMorse.put(".", ".-.-.-");
		letterToMorse.put(",", "--..--");
		letterToMorse.put("!", "-.-.--");
		letterToMorse.put("?", "..--..");
		letterToMorse.put(":", "---...");
		letterToMorse.put(";", "-.-.-.");
		letterToMorse.put("'", ".----.");
		letterToMorse.put("-", "-....-");
		letterToMorse.put("/", "-..-.");
		letterToMorse.put("@", ".--.-.");
		letterToMorse.put("=", "-...-");

		if (morseToLetter == null)
			morseToLetter = new TreeMap<String, String>();

		morseToLetter.clear();
		for (String key : letterToMorse.keySet()) {
			morseToLetter.put(letterToMorse.get(key), key);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	@Override
	public String format() {
		return super.format() + " [to | from] {text | morse}";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 3)) {
			invalidFormat(bot, chan, user);
		} else {
			String[] splitMessage = message.split(" ", 3);
			String output = "";

			switch (splitMessage[1]) {
			case "to":
				output = toMorse(splitMessage[2]);
				break;
			case "from":
				output = fromMorse(splitMessage[2]);
				break;
			}

			String target = getTarget(chan, user);

			String[] outputs;
			if (!target.equals(user.getNick())) {
				outputs = new String[1];
				outputs[0] = truncateOutput(output);
			} else
				outputs = output.split("(?<=\\G.{340})");

			int outputLineNum = 1;
			for (String outputLine : outputs)
				passMessage(bot, chan, user, "Output (line "
						+ (outputLineNum++) + "): " + outputLine);
		}
	}

	/**
	 * To morse.
	 * 
	 * @param input
	 *            the input
	 * @return the string
	 */
	public String toMorse(String input) {
		input = input.toLowerCase();
		String output = "";
		for (int i = 0; i < input.length(); i++) {
			String currentChar = "" + input.charAt(i);

			if (output.length() > 0)
				output += " ";
			if (letterToMorse.containsKey(currentChar))
				output += letterToMorse.get(currentChar);
			else
				output += "[" + currentChar + "]";
		}
		return output;
	}

	/**
	 * From morse.
	 * 
	 * @param input
	 *            the input
	 * @return the string
	 */
	public String fromMorse(String input) {
		String output = "";
		String[] choppedInput = input.split(" ", 0);
		for (String part : choppedInput) {
			if (part.length() == 0)
				continue;

			if (morseToLetter.containsKey(part))
				output += morseToLetter.get(part);
			else
				output += "[" + part + "]";
		}
		return output;
	}

	/**
	 * Truncate output.
	 * 
	 * @param text
	 *            the text
	 * @return the string
	 */
	private String truncateOutput(String text) {
		if (text.length() > 340)
			return text.substring(0, 340)
					+ " (truncated at 340 symbols; use in a PM to get full output)";
		else
			return text;
	}
}
