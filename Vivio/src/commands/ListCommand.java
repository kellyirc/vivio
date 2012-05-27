package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

public class ListCommand extends Command {
	
	//TODO list active|inactive
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		passMessage(bot, chan, user, "Current modules are: "+ bot.getModules());
		
	}

	@Override
	protected void initialize() {
		setName("List");
		addAlias("list");
		this.setHelpText("I'll tell you what all of my modules are!");
	}

}
