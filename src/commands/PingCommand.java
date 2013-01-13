/*
 * @author Kyle Kemp
 * @description This module allows a user to 'ping' every other user in a given channel, by saying their respective nicknames all at once.
 * @basecmd ping
 * @category misc
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

// TODO: Auto-generated Javadoc
/**
 * The Class PingCommand.
 */
public class PingCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		String s = "";
		for (User u : chan.getUsers()) {
			s += u.getNick() + " ";
		}
		passMessage(bot, chan, user, "PING! " + s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("ping");
		setName("Ping");
		setHelpText("Ping all users in the channel. Don't annoy them too much, now!");
	}

}
