package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

public class DieCommand extends Command {
	
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if(Util.hasArgs(message, 3)) {
			String[] args = Util.getArgs(message, 3);
			if(args[1].equals("server")) {
				bot.quitServer(args[2]);
			} else {
				for(Channel c : bot.getChannels()) {
					bot.partChannel(c, args[2]);
				}
			}
		} else if(Util.hasArgs(message, 2)) {
			String[] args = Util.getArgs(message, 2);
			if(args[1].equals("server")) {
				bot.quitServer();
			} else {
				for(Channel c : bot.getChannels()) {
					bot.partChannel(c);
				}
			}
		} else {
			invalidFormat(bot, chan, user);
		}
	}

	@Override
	protected void initialize() {
		addAlias("die");
		addAlias("quit");
		setName("Die");
		setHelpText("Kill me from the server.. possibly!");
		setAccessLevel(LEVEL_OWNER);
	}
	
	protected String format() {
		return super.format() + " [all|server] {reason}";
	}

}
