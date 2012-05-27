package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

public class DiceCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if(!Util.checkArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}
		
		String dice = Util.getArgs(message, 2)[1];
		
		String[] args = dice.split("d");
		
		int total = 0;
		
		try {
			int rolls = Integer.parseInt(args[0]);
			int size = Integer.parseInt(args[1]);

			while(rolls-->0) {
				total += random.nextInt(size)+1;
			}
		}catch(Exception e) {
			passMessage(bot, chan, user, user.getNick() + ", your roll was invalid.");
			return;
		}
		
		passMessage(bot, chan, user, user.getNick() + ", your "+dice+" rolled "+total+".");
		
	}

	@Override
	protected void initialize() {
		addAlias("roll");
		addAlias("dice");
		setName("DiceRoller");
		setHelpText("Roll some dice, D&D style!");
	}

	protected String format() {
		return super.format() + " [xdy]";
	}
	
}
