/*
 * @author Kyle Kemp
 * @description This module allows a user to create a new bot on a new server using the proper command line switches, as if IRC were the command line.
 * @basecmd new
 * @category core
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Initializer;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class NewBotCommand.
 */
public class NewBotCommand extends Command {

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
		// TODO make it so there can't be multiple bots per server
		Initializer.parseCommands(message.substring(message.indexOf(" "))
				.split(" "));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format()
				+ " [-s server | -p port | -c channel | -o owner | -pass server-password | -n nickserv-password] [--use-ssl]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("new");
		this.setHelpText("Spawn a new meeee!");
		this.setName("NewBot");
		this.setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

}
