/*
 * @author Kyle Kemp
 * @description This module allows a user to tell the bot to either quit all channels or leave the server completely.
 * @basecmd die
 * @category misc
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class DieCommand.
 */
public class DieCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (Util.hasArgs(message, 3)) {
			String[] args = Util.getArgs(message, 3);
			if (args[1].equals("server")) {
				bot.quitServer(args[2]);
			} else {
				for (Channel c : bot.getChannels()) {
					bot.partChannel(c, args[2]);
				}
			}
		} else if (Util.hasArgs(message, 2)) {
			String[] args = Util.getArgs(message, 2);
			if (args[1].equals("server")) {
				bot.quitServer();
			} else {
				for (Channel c : bot.getChannels()) {
					bot.partChannel(c);
				}
			}
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
		addAlias("die");
		addAlias("quit");
		setName("Die");
		setHelpText("Kill me from the server.. possibly!");
		setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [all|server] {reason}";
	}

}
