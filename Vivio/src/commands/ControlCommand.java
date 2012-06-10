package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

public class ControlCommand extends Command {

	//TODO op, deop, voice, devoice, channel mode, ban, kick, etc etc
	
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {	}
	
	public void execute(String alias, Bot bot, Channel chan, User user, String message) {
		switch(alias) {
		case "echo":
			echo(bot, chan, user, message);
			return;
		case "emote":
			emote(bot, chan, user, message);
			return;
		}
	}

	private void emote(Bot bot, Channel chan, User user, String message) {
		if(!Util.hasArgs(message, 3)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 3);
		
		passEmote(bot, bot.getChannel(args[1]), user, args[2]);
		
	}

	private void echo(Bot bot, Channel chan, User user, String message) {
		if(!Util.hasArgs(message, 3)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 3);
		
		passMessage(bot, bot.getChannel(args[1]), user, args[2]);
	}

	@Override
	protected void initialize() {
		setName("Control");
		setHelpText("Control various aspects of the bot.");
		addAlias("echo");
		addAlias("emote");
		setUsableInPM(true);
		setAccessLevel(LEVEL_ELEVATED);
	}

	protected String format() {
		return super.format() + " [#chan | nick] [message]";
	}
	
}
