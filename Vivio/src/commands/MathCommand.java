package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

public class MathCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if(!Util.checkArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}
		
		final com.seeingwithc.math.MathEvaluator me = new com.seeingwithc.math.MathEvaluator(message.substring(message.split(" ")[0].length()));
		try {
			passMessage(bot, chan, user, user.getNick() + ", your answer is "+me.getValue());
		} catch (Exception e){
			passMessage(bot, chan, user, user.getNick() + ", your problem could not be processed.");
		}
	}

	protected String format() {
		return super.format() + " [expression]";
	}
	
	@Override
	protected void initialize() {
		addAlias("math");
		addAlias("calc");
		setHelpText("Calculate a string of mathematical expressions.");
		setName("Math");
	}

}
