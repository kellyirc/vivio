package commands;

import modules.Module;

import org.pircbotx.Channel;
import org.pircbotx.User;


import backend.Bot;
import backend.Util;

public class ToggleCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if(!Util.hasArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 2);
		
		for(Module m : bot.getModules()){
			if(m.getName().toLowerCase().equals(args[1].toLowerCase())) {
				if(!(Bot.getLevelForUser(user, chan) >= m.getAccessLevel())) {
					passMessage(bot, chan, user, "You do not have the proper privileges to execute this action.");
					return;
				}
				
				if(m.isActive()) {
					passMessage(bot, chan, user, m.getName() + " is now OFF.");
					m.setActive(false);
				} else {
					passMessage(bot, chan, user, m.getName() + " is now ON.");
					m.setActive(true);
				}
				return;
			}
		}
		
		passMessage(bot, chan, user, "The module "+args[1]+" does not exist.");
	}
	
	protected String format() {
		return super.format() + " [module]";
	}

	@Override
	protected void initialize() {
		addAlias("toggle");
		setName("Toggle");
		this.setAccessLevel(LEVEL_OPERATOR);
		this.setHelpText("I can toggle other modules on and off, whee!");
	}

	public void setActive(boolean active) {return;}
}
