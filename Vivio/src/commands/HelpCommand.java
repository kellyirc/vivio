package commands;

import modules.Module;

import org.pircbotx.Channel;
import org.pircbotx.User;


import backend.Bot;
import backend.Util;

public class HelpCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if(!Util.checkArgs(message, 2)) {
			passMessage(bot, chan, user, "Invalid format: " + format());
			return;
		}
		String[] args = Util.getArgs(message, 2);
		
		for(Module mod : bot.getModules()) {
			if(mod instanceof Command) {
				Command c = (Command) mod;
				if(c.getAliases().contains(args[1]) || c.getName().toLowerCase().equals(args[1].toLowerCase())) {
					passMessage(bot, chan, user, c.format() + " -- " + c.getHelpText());
					return;
				}
			}
		}
	}
	
	@Override
	protected void initialize() {
		this.setPriorityLevel(PRIORITY_MEDIUM);
		this.setHelpText("I can give you information about the other commands!");
		getAliases().add("help");
		this.setName("Help");
	}
	
	protected String format() {
		return super.format() + " [module]";
	}

}
