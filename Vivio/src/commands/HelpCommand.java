/*
 * @author Kyle Kemp
 * @description This module allows a user to get additional information and syntax help for another module.
 * @basecmd help
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
 * The Class HelpCommand.
 */
public class HelpCommand extends Command {

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

		for (Module mod : bot.getModules()) {
			if (mod instanceof Command) {
				Command c = (Command) mod;
				if (c.getAliases().contains(args[1])
						|| c.getName().toLowerCase()
								.equals(args[1].toLowerCase())) {
					passMessage(bot, chan, user,
							c.format() + " -- " + c.getHelpText() + " "
									+ formatLevel(c.getAccessLevel()));
					return;
				}
			}
		}
	}

	/**
	 * Format level.
	 * 
	 * @param accessLevel
	 *            the access level
	 * @return the string
	 */
	private String formatLevel(short accessLevel) {
		switch (accessLevel) {
		case LEVEL_OWNER:
			return " (Owners)";
		case LEVEL_ELEVATED:
			return " (Elevated Users)";
		case LEVEL_OPERATOR:
			return " (Operators)";
		case LEVEL_BANNED:
			return " (Even Banned Users!)";
		default:
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		this.setPriorityLevel(PRIORITY_MEDIUM);
		this.setHelpText("I can give you information about the other commands!");
		addAlias("help");
		this.setName("Help");
		setUsableInPM(true);
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
	 * @see modules.Module#setActive(boolean)
	 */
	public void setActive(boolean active) {
		return;
	}
}
