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

public class UptimeCommand extends Command {
	
	//TODO make this support times as they go on (ie, only show seconds if necessary, show up to weeks in time)
	
 	static long startTime = System.currentTimeMillis();

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		passMessage(bot, chan, user, "Current uptime is "+Util.getElapsedTimeHoursMinutesSecondsString(startTime));
	}
	
	@Override
	protected void initialize() {
		addAlias("uptime");
		this.setHelpText("See how long I've been going ;)");
		this.setName("Uptime");
		setUsableInPM(true);
	}

}
