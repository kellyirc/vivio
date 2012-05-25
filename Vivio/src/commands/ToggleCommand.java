package commands;

import modules.Module;

import org.pircbotx.Channel;
import org.pircbotx.User;

import shared.Util;

import bot.Bot;

public class ToggleCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if(!Util.checkArgs(message, 2)) {
			passMessage(bot, chan, user, "Invalid format: " + format() + " [module]");
			return;
		}
		String[] args = Util.getArgs(message, 2);
		
		for(Module m : bot.getModules()){
			if(m.getName().toLowerCase().equals(args[1].toLowerCase())) {
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

	@Override
	protected void initialize() {
		getAliases().add("toggle");
		setName("Toggle");
	}

}
