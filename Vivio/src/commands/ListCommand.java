package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import bot.Bot;

public class ListCommand extends Command {
	
	//TODO list active|inactive
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		passMessage(bot, chan, user, "Current modules are: "+ bot.getModules());
		
	}

	@Override
	protected void initialize() {
		setName("List");
		getAliases().add("list");
		this.setHelpText("I'll tell you what all of my modules are!");
	}

}
