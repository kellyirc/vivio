package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;


import backend.Bot;
import backend.Util;

public class QuietCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if(!Util.checkArgs(message, 2)) {
			passMessage(bot, chan, user, "Invalid format: " + format());
			return;
		}
		String[] args = Util.getArgs(message, 2);
		
		if(Bot.getBanned().contains(args[1].toLowerCase())) {
			Bot.removeBanned(args[1]);
			passMessage(bot, chan, user, "I am no longer quiet for "+args[1]);
		} else {
			passMessage(bot, chan, user, "I will now be quiet for "+args[1]);
			Bot.addBanned(args[1]);
		}
	}
	
	protected String format() {
		return super.format() + " [user | channel]";
	}

	@Override
	protected void initialize() {
		getAliases().add("quiet");
		getAliases().add("ignore");
		this.setPriorityLevel(PRIORITY_HIGH);
		this.setAccessLevel(LEVEL_OPERATOR);
		this.setName("Quiet");
		this.setStopsExecution(true);
		this.setHelpText("I can stop talking to another person, or stop talking to a channel with this toggled!");
	}

}
