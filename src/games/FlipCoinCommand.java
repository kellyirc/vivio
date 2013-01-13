/*
 * @author Rahat Ahmed
 * @description Flips a coin and outputs the result.
 * @basecmd flip
 * @category games
 */
package games;

import java.util.ArrayList;
import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

import commands.Command;

public class FlipCoinCommand extends Command
{
	
	Random rand;
	ArrayList<String> responses;
	
	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
		passMessage(bot, chan, user, flip());
	}

	@Override
	protected void initialize()
	{
		setHelpText("Flips a coin and outputs the result.");
		addAlias("flip");
		addAlias("flipcoin");
		setName("FlipCoin");
		rand = new Random();
		responses = new ArrayList<>();
		responses.add("The coin lands inside my mouth. Nobody knows what it was!");
		responses.add("The coin disappears!");
		responses.add("The coin opens a black hole. The coin and all your hopes of finding out what it was are sucked into it!");
		responses.add("What are you looking at me for? Hmm? What coin?");
		responses.add("The coin stops in mid-air. The coin is staring at you. A voice whispers \"Today is your last day. Enjoy it before I end you\".");
		responses.add("CoinFlipException!");
	}

	private String flip()
	{
		String ret = "A coin is flipped. The result is... ";
		double flip = rand.nextDouble()*100;
		if(between(flip, 0, 48))
			ret += "Heads.";
		else if(between(flip, 48, 98))
			ret += "Tails.";
		else if(between(flip, 98, 99))
			ret += "The coin landed on its side.";
		else
		{
			flip -= 99;
			flip *= responses.size();
			ret += responses.get((int) flip);
		}
		return ret;
	}
	
	private boolean between(double n, double lower, double upper)
	{
		return n>=lower && n<upper;
	}
	
}
