/*
 * @author Rahat Ahmed
 * @description Chooses between given choices.
 * @basecmd choose
 * @category games
 */
package games;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;
import commands.Command;

public class ChooseCommand extends Command
{

	@Override
	protected void initialize()
	{
		setHelpText("Chooses something for you. Delimiters: or , |");
		addAlias("choose");
		setName("Choose");
	}

	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
		String choice = choose(message.split(" ",2)[1]);
		passMessage(bot, chan, user, "I choose "+choice+".");
	}
	
	private String choose(String choices)
	{
		String[] array = choices.split("\\bor\\b|,|\\|");
		return array[(int) (Math.random()*array.length)].trim();
	}
	
	@Override
	protected String format()
	{
		return super.format() + "[choice] (delimiter) [choice] (delimiter) [choice] ... ";
	}
}
