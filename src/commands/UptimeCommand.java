/*
 * @author Kyle Kemp
 * @description This module allows a user to check how long the bot has been online.
 * @basecmd uptime
 * @category misc
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class UptimeCommand.
 */
public class UptimeCommand extends Command {

	// TODO make this support times as they go on (ie, only show seconds if
	// necessary, show up to weeks in time)

	/** The start time. */
	static long startTime = System.currentTimeMillis();

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		passMessage(
				bot,
				chan,
				user,
				"Current uptime is "
						+ Util.getElapsedTimeHoursMinutesSecondsString(startTime));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("uptime");
		this.setHelpText("See how long I've been going ;)");
		this.setName("Uptime");
		setUsableInPM(true);
	}

}
