package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import shared.Initializer;
import shared.Util;

import bot.Bot;

public class NewBotCommand extends Command{

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if(!Util.checkArgs(message, 2)) {
			passMessage(bot, chan, user, "Invalid format: " + format());
			return;
		}
		
		Initializer.parseCommands(message.substring(message.indexOf(" ")).split(" "));
	}
	
	protected String format() {
		return super.format() + " [-s server | -p port | -c channel | -o owner | -pass server-password | -n nickserv-password] [--use-ssl]";
	}
	
	@Override
	protected void initialize() {
		getAliases().add("new");
		this.setHelpText("Spawn a new meeee!");
		this.setName("NewBot");
		this.setAccessLevel(LEVEL_OWNER);
	}

}
