package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

public class NickChangeCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if(Util.hasArgs(message, 3)){
			String[] args = Util.getArgs(message,2);
			bot.changeNick(args[1]);
			bot.identify(args[2]);
		}else if(Util.hasArgs(message, 2)){
			bot.changeNick(Util.getArgs(message,2)[1]);
		} else {
			invalidFormat(bot, chan, user);
		}
	}

	@Override
	protected void initialize() {
		addAlias("nick");
		setName("NickChange");
		setHelpText("Change my nickname!");
		setAccessLevel(LEVEL_ELEVATED);
		setUsableInPM(true);
		
	}
	
	protected String format() {
		return super.format() + " [nickname]";
	}

}
