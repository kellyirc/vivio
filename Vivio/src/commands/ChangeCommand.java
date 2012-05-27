package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

public class ChangeCommand extends Command{

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		//verbose
		//parseself
		if(!Util.checkArgs(message, 3)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 3);
		switch(args[1]) {
		case "verbose":
			if(Integer.parseInt(args[2]) > 0) bot.setVerbose(true);
			else bot.setVerbose(false);
			break;
		case "parseself":
			if(Integer.parseInt(args[2]) > 0) bot.setParsesSelf(true);
			else bot.setParsesSelf(false);
			break;
		}
		passMessage(bot, chan, user, "So it is said, and so it shall be!");
	}

	@Override
	protected void initialize() {
		addAlias("change");
		addAlias("internal");
		setName("ChangeInternals");
		setHelpText("Change internal variables! Ooooh la la!");
		this.setAccessLevel(LEVEL_OWNER);
	}
	
	protected String format() {
		return super.format() + " [internal_var] [val]";
	}
}
