/*
 * @author Kyle Kemp
 * @description This module allows a user to have the bot join another channel.
 * @basecmd join
 * @category core
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class JoinCommand.
 */
public class JoinCommand extends Command {

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
			if (bot.isInChannel(args[1])) {
				bot.partChannel(bot.getChannel(args[1]), args[2]);
				passMessage(bot, chan, user, "I have left " + args[1] + "("
						+ args[2] + ")");
			} else {
				bot.joinChannel(args[1], args[2]);
				passMessage(bot, chan, user, "I have joined " + args[1]);
			}
		} else if (Util.hasArgs(message, 2)) {
			String[] args = Util.getArgs(message, 2);
			if (bot.isInChannel(args[1])) {
				bot.partChannel(bot.getChannel(args[1]));
				passMessage(bot, chan, user, "I have left " + args[1]);
			} else {
				bot.joinChannel(args[1]);
				passMessage(bot, chan, user, "I have joined " + args[1]);
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
		addAlias("join");
		addAlias("part");
		setName("JoinPart");
		setHelpText("Join or part a channel.. I'm so multi-purpose!");
		setAccessLevel(LEVEL_ELEVATED);
		setUsableInPM(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [channel] {key}";
	}

}
