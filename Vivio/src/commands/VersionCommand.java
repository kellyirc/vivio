/*
 * @author Kyle Kemp
 * @description This module lets you see the version of the bot.
 * @basecmd version
 * @category core
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

public class VersionCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		passMessage(bot, chan, user, "I am currently running version "+Bot.INTERNAL_VERSION+". You can see the most recent changes here: https://code.google.com/p/vivio/source/list");
	}

	@Override
	protected void initialize() {
		setName("Version");
		setHelpText("Check my version. In case you were wondering, it's "+Bot.INTERNAL_VERSION+"!");
		addAlias("version");
		setUsableInPM(true);
	}

}
