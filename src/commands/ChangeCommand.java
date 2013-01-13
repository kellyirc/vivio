/*
 * @author Kyle Kemp
 * @description This module allows for the changing of internal bot variables, such as verbosity, parsing, and CLI mode.
 * @basecmd change
 * @category utility
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class ChangeCommand.
 */
public class ChangeCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 3)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 3);
		switch (args[1]) {
		case "verbose":
			if (Integer.parseInt(args[2]) > 0)
				bot.setVerbose(true);
			else
				bot.setVerbose(false);
			break;
		case "parseself":
			if (Integer.parseInt(args[2]) > 0)
				bot.setParsesSelf(true);
			else
				bot.setParsesSelf(false);
			break;
		case "cli":
			if (Integer.parseInt(args[2]) > 0)
				bot.setParsesCmd(true);
			else
				bot.setParsesCmd(false);
			break;
		case "log":
			if (Integer.parseInt(args[2]) > 0)
				bot.setLogsSelf(true);
			else
				bot.setLogsSelf(false);
			break;
		}
		passMessage(bot, chan, user, "So it is said, and so it shall be!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("change");
		addAlias("internal");
		setName("ChangeInternals");
		setHelpText("Change internal variables! Ooooh la la!");
		this.setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [verbose | parseself | cli] [val]";
	}
}
