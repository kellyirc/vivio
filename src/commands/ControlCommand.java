/*
 * @author Kyle Kemp
 * @description This module allows for the remote management of a channel. This includes features such as talking, emoting, and other functions such as opping, kicking, and changing the topic.
 * @basecmd control
 * @category core
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class ControlCommand.
 */
public class ControlCommand extends Command {

	// TODO op, deop, voice, devoice, channel mode, ban, kick, etc etc

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(java.lang.String, backend.Bot,
	 * org.pircbotx.Channel, org.pircbotx.User, java.lang.String)
	 */
	public void execute(String alias, Bot bot, Channel chan, User user,
			String message) {
		switch (alias) {
		case "echo":
			echo(bot, chan, user, message);
			return;
		case "emote":
			emote(bot, chan, user, message);
			return;
		}
	}

	/**
	 * Emote.
	 * 
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param user
	 *            the user
	 * @param message
	 *            the message
	 */
	private void emote(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 3)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 3);

		passEmote(bot, bot.getChannel(args[1]), user, args[2]);

	}

	/**
	 * Echo.
	 * 
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param user
	 *            the user
	 * @param message
	 *            the message
	 */
	private void echo(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 3)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 3);

		passMessage(bot, args[1].startsWith("#") ? bot.getChannel(args[1])
				: null, !args[1].startsWith("#") ? bot.getUser(args[1]) : null,
				args[2]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		setName("Control");
		setHelpText("Control various aspects of the bot.");
		addAlias("echo");
		addAlias("emote");
		addAlias("control");
		setUsableInPM(true);
		setAccessLevel(LEVEL_ELEVATED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [#chan | nick] [message]";
	}

}
