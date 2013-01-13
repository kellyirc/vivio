/*
 * @author Rahat Ahmed
 * @description Gives advice for all your yes and no questions.
 * @basecmd 8ball
 * @category games
 */
package games;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;
import backend.Util;

import commands.Command;

public class EightBallCModule extends Command
{
	
	String[] responses;

	@Override
	protected void initialize()
	{
		setHelpText("Gives advice for all your yes and no questions.");
		addAlias("8ball");
		setName("8Ball");
		responses = "Yes\nNo\nDon't count on it\nNot counting on it\nMight just be so\nYeah, sure\nWhat I know of, it might just be true\nAs I see it, yes\nAs I see it, no\nI don't think so\nI think so\nYeah\nNah\nYea\nNay\nThe majority of my sources say \"YES\"\nThe majority of my sources say \"NO\"\nWeakling question, NEXT!\nMy answer is... YES\nMy Answer is... NO\nWhy should I bother answering?\nYou go figure it out\nCan't be arsed to answer\nAsk again later\nLikely\nNot likely\nFuck off, I'm not going to answer right now\nNuh-uh\nUh-huh\nSomething along those lines\nNo way in hell\nDoubtful, really\nWithout a doubt\nNaw\nYup\nYeeeeeuuuuuup\nNonono\nStop bothering me, I'm busy!\nThink about it\nWhen pigs fly\nMyth Confirmed\nMyth Busted\nLookin' good, alright\nYeah no, outlook NOT good\nYou wish\nAsk me tomorrow\nFosho!\nLolno!\ntl;dr\nMmmyeah\nMmmno\nSurely\nAin't no way that'd be true\nObviously yes\nObviously no\nThink about your question, then try again\nConsult your beloved\nAsk someone else\nOnly time will tell...\nWhat do you think?".split("\\n");
	}

	@Override
	public void onMessage(MessageEvent<Bot> e) throws Exception
	{
		super.onMessage(e);
		String msg = e.getMessage();
		String nick = e.getBot().getNick();
		if((msg.startsWith(nick+",")||msg.startsWith(nick+":")) && msg.endsWith("?"))
		{
			execute(e.getBot(), e.getChannel(), e.getUser(), e.getMessage());
		}
	}
	
	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
		if(Util.hasArgs(message,2))
		{
			passMessage(bot, chan, user, getResponse());
		}
		else
		{
			passMessage(bot, chan, user, "Try asking a question next time.");
		}
	}
	
	@Override
	protected String format()
	{
		return  "[bot-nick], [question]?";
	}
	
	private String getResponse()
	{
		return responses[(int) (Math.random()*responses.length)];
	}
}
