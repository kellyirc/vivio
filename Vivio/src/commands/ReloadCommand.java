package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

public class ReloadCommand extends Command{

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		long start = System.currentTimeMillis();
		bot.loadModules();
		passMessage(bot, chan, user, "Done reloading! It took "+(System.currentTimeMillis()-start)+" ms.");
	}
	
	@Override
	public void initialize() {
		addAlias("reload");
		this.setHelpText("Reload all of my insides, woo!");
		this.setName("Reload");
		this.setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

	public void setActive(boolean active) {return;}
}
