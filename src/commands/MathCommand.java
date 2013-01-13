/*
 * @author Kyle Kemp
 * @description This module allows a user to calculate a mathematical expression.
 * @basecmd math
 * @category utility
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class MathCommand.
 */
public class MathCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}

		final com.seeingwithc.math.MathEvaluator me = new com.seeingwithc.math.MathEvaluator(
				message.substring(message.split(" ")[0].length()));
		try {
			passMessage(bot, chan, user, user.getNick() + ", your answer is "
					+ me.getValue());
		} catch (Exception e) {
			passMessage(bot, chan, user, user.getNick()
					+ ", your problem could not be processed.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [expression]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("math");
		addAlias("calc");
		addAlias("calculate");
		setHelpText("Calculate a string of mathematical expressions.");
		setName("Math");
		setUsableInPM(true);
	}

}
