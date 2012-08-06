/*
 * @author Rahat Ahmed
 * @description This module allows for the translation between languages using Google Translate.
 * @basecmd translate
 * @category utility
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class TranslateCommand.
 */
public class TranslateCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		setHelpText("Translate from one language to another via Google Translate. The from and to languages should be specified by their ISO 639-1 Code.");
		setName("Translate");
		addAlias("translate");
		Translate.setKey("8867C076B95F315D1B38A380C9483A694EE865E3");
		setUsableInPM(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (Util.hasArgs(message, 4)) {
			String args[] = message.split(" ", 4);
			passMessage(bot, chan, user, translate(args[1], args[2], args[3]));
		} else
			invalidFormat(bot, chan, user);
		// if(numArgs <3)
		// event.getBot().sendNotice(getUser(event),
		// "Usage: !translate [from] [to] [text]");
		// else
		// {
		// String[] cmd = args.split(" ", 3);
		// event.getBot().sendMessage(getTarget(event),
		// translate(cmd[0],cmd[1],cmd[2]));
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	@Override
	protected String format() {
		return super.format() + " [from] [to] [text]";
	}

	/**
	 * Translate.
	 * 
	 * @param startingLanguage
	 *            the starting language
	 * @param endLanguage
	 *            the end language
	 * @param toTranslate
	 *            the to translate
	 * @return the string
	 */
	private static String translate(String startingLanguage,
			String endLanguage, String toTranslate) {
		try {
			Translate.setHttpReferrer("http://rahat.seiyria.com");
			Language sl = Language.fromString(startingLanguage);
			Language el = Language.fromString(endLanguage);
			return Translate.execute(toTranslate, sl, el);
		} catch (Exception e) {
			e.printStackTrace();
			return "Could not translate. Probably due to bad language names";
		}

	}
}
