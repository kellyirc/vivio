/*
 * @author Kyle Kemp
 * @description This module allows for the toggling on and off of other modules.
 * @basecmd toggle
 * @category core
 */
package commands;

import modules.Module;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class ToggleCommand.
 */
public class ToggleCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if (!Util.hasArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 2);

		for (Module m : bot.getModules()) {
			if (m.getName().toLowerCase().equals(args[1].toLowerCase())) {
				if (!(Bot.getLevelForUser(user, chan) >= m.getAccessLevel())) {
					passMessage(bot, chan, user,
							"You do not have the proper privileges to execute this action.");
					return;
				}

				if (m.isActive()) {
					passMessage(bot, chan, user, m.getName() + " is now OFF.");
					m.setActive(false);
				} else {
					passMessage(bot, chan, user, m.getName() + " is now ON.");
					m.setActive(true);
				}
				return;
			}
		}

		passMessage(bot, chan, user, "The module " + args[1]
				+ " does not exist.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [module]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("toggle");
		setName("Toggle");
		this.setAccessLevel(LEVEL_OPERATOR);
		this.setPriorityLevel(PRIORITY_HIGH);
		this.setHelpText("I can toggle other modules on and off, whee!");
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
