package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;


import backend.Bot;
import backend.Initializer;
import backend.Util;

public class NewBotCommand extends Command{

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if(!Util.hasArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}
		//TODO make it so there can't be multiple bots per server
		Initializer.parseCommands(message.substring(message.indexOf(" ")).split(" "));
	}
	
	protected String format() {
		return super.format() + " [-s server | -p port | -c channel | -o owner | -pass server-password | -n nickserv-password] [--use-ssl]";
	}
	
	@Override
	protected void initialize() {
		addAlias("new");
		this.setHelpText("Spawn a new meeee!");
		this.setName("NewBot");
		this.setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

}
