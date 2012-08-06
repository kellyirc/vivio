/*
 * @author Kyle Kemp
 * @description This module allows a user to roll a set of x-sided dice, y times. 
 * @basecmd roll
 * @category util
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class DiceCommand.
 */
public class DiceCommand extends Command {

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

		String dice = Util.getArgs(message, 2)[1];

		String[] args = dice.split("d");

		int total = 0;

		try {
			int rolls = Integer.parseInt(args[0]);
			int size = Integer.parseInt(args[1]);

			while (rolls-- > 0) {
				total += random.nextInt(size) + 1;
			}
		} catch (Exception e) {
			passMessage(bot, chan, user, user.getNick()
					+ ", your roll was invalid.");
			return;
		}

		passMessage(bot, chan, user, user.getNick() + ", your " + dice
				+ " rolled " + total + ".");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("roll");
		addAlias("dice");
		setName("DiceRoller");
		setHelpText("Roll some dice, D&D style!");
		setUsableInPM(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [xdy]";
	}

}
