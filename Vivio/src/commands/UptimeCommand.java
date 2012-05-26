package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

public class UptimeCommand extends Command {
	
 	static long startTime = System.currentTimeMillis();

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		passMessage(bot, chan, user, "Current uptime is "+getElapsedTimeHoursMinutesSecondsString());
	}
	
	@Override
	protected void initialize() {
		getAliases().add("uptime");
		this.setHelpText("See how long I've been going ;)");
		this.setName("Uptime");
	}

	private String getElapsedTimeHoursMinutesSecondsString() {     
	    long elapsedTime = System.currentTimeMillis()-startTime;
	    String format = String.format("%%0%dd", 2);
	    elapsedTime = elapsedTime / 1000;
	    String seconds = String.format(format, elapsedTime % 60);
	    String minutes = String.format(format, (elapsedTime % 3600) / 60);
	    String hours = String.format(format, elapsedTime / 3600);
	    String time =  hours + " hours, " + minutes + " minutes, " + seconds+ " seconds";
	    return time;
	}

}
