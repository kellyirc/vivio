package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

public class JoinCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
			if(Util.checkArgs(message, 3)) {
				String[] args = Util.getArgs(message, 3);
				args[1] = Util.formatChannel(args[1]);
				if(bot.isInChannel(args[1])) {
					bot.partChannel(bot.getChannel(args[1]), args[2]);
					passMessage(bot, chan, user, "I have left "+args[1] + "("+args[2]+")");
				} else {
					bot.joinChannel(args[1], args[2]);
					passMessage(bot, chan, user, "I have joined "+args[1]);
				}
			} else if(Util.checkArgs(message, 2)) {
				String[] args = Util.getArgs(message, 2);
				args[1] = Util.formatChannel(args[1]);
				if(bot.isInChannel(args[1])) {
					bot.partChannel(bot.getChannel(args[1]));
					passMessage(bot, chan, user, "I have left "+args[1]);
				} else {
					bot.joinChannel(args[1]);
					passMessage(bot, chan, user, "I have joined "+args[1]);
				}
			} else {
				invalidFormat(bot, chan, user);
			}
	}

	@Override
	protected void initialize() {
		addAlias("join");
		addAlias("part");
		setName("JoinPart");
		setHelpText("Join or part a channel.. I'm so multi-purpose!");
		setAccessLevel(LEVEL_ELEVATED);
	}
	
	protected String format() {
		return super.format() + " [channel] {key}";
	}

}
