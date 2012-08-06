/*
 * @author Kyle Kemp
 * @description This module allows a user to change the bots nickname.
 * @basecmd nick
 * @category core
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class NickChangeCommand.
 */
public class NickChangeCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if (Util.hasArgs(message, 3)) {
			String[] args = Util.getArgs(message, 2);
			bot.changeNick(args[1]);
			bot.identify(args[2]);
		} else if (Util.hasArgs(message, 2)) {
			bot.changeNick(Util.getArgs(message, 2)[1]);
		} else {
			invalidFormat(bot, chan, user);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("nick");
		setName("NickChange");
		setHelpText("Change my nickname!");
		setAccessLevel(LEVEL_ELEVATED);
		setUsableInPM(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [nickname]";
	}

}
