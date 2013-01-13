/*
 * @author Kyle Kemp
 * @description This module allows an operator to tell the bot to be quiet in a channel, if it were to get annoying.
 * @basecmd quiet
 * @category core
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class QuietCommand.
 */
public class QuietCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 2);

		if (Bot.getBanned().contains(args[1].toLowerCase())) {
			Bot.removeBanned(args[1]);
			passMessage(bot, chan, user, "I am no longer quiet for " + args[1]);
		} else {
			passMessage(bot, chan, user, "I will now be quiet for " + args[1]);
			Bot.addBanned(args[1]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [user | channel]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("quiet");
		addAlias("ignore");
		this.setPriorityLevel(PRIORITY_HIGH);
		this.setAccessLevel(LEVEL_OPERATOR);
		this.setName("Quiet");
		this.setStopsExecution(true);
		setUsableInPM(true);
		this.setHelpText("I can stop talking to another person, or stop talking to a channel with this toggled!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#setActive(boolean)
	 */
	public void setActive(boolean active) {
		return;
	}
}
