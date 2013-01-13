/*
 * @author Kyle Kemp
 * @description This module allows a user to reload all of the modules in the bot.
 * @basecmd reload
 * @category core
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

// TODO: Auto-generated Javadoc
/**
 * The Class ReloadCommand.
 */
public class ReloadCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		long start = System.currentTimeMillis();
		bot.loadModules();
		passMessage(bot, chan, user,
				"Done reloading! It took "
						+ (System.currentTimeMillis() - start) + " ms.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	public void initialize() {
		addAlias("reload");
		this.setHelpText("Reload all of my insides, woo!");
		this.setName("Reload");
		this.setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#setActive(boolean)
	 */
	public void setActive(boolean active) {
		return;
	}
}
