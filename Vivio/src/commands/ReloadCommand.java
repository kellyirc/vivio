package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import bot.Bot;

public class ReloadCommand extends Command{

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		bot.loadModules();
		passMessage(bot, chan, user, "Reloading myself!");
	}
	
	@Override
	public void initialize() {
		getAliases().add("reload");
		this.setHelpText("Reload all of my insides, woo!");
		this.setName("Reload");
	}

}
