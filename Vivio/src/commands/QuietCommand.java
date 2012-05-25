package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import shared.Util;

import bot.Bot;

public class QuietCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if(!Util.checkArgs(message, 2)) {
			passMessage(bot, chan, user, "Invalid format: " + format() + " [user]|[channel]");
			return;
		}
		String[] args = Util.getArgs(message, 2);
		
		if(Bot.getBanned().contains(args[1])) {
			Bot.removeBanned(args[1]);
			passMessage(bot, chan, user, "I am no longer quiet for "+args[1]);
		} else {
			passMessage(bot, chan, user, "I will now be quiet for "+args[1]);
			Bot.addBanned(args[1]);
		}
	}

	@Override
	protected void initialize() {
		getAliases().add("quiet");
		getAliases().add("ignore");
		this.setPriorityLevel(PRIORITY_HIGH);
		this.setName("Quiet");
		this.setStopsExecution(true);
	}

}
