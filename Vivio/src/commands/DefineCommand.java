package commands;

import java.util.List;

import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.dto.Definition;
import net.jeremybrooks.knicker.dto.Example;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;


import backend.Bot;
import backend.Util;

public class DefineCommand extends Command
{

	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
		if(Util.checkArgs(message, 2))
			passMessage(bot, chan, user, define(message.split(" ", 2)[1]));
		else
			invalidFormat(bot, chan, user);
	}

	@Override
	protected void initialize()
	{
		System.setProperty("WORDNIK_API_KEY", "406ed874a052a17533000020f430da862109779ddb217756f");
		setHelpText("Gets the definition of english words.");
		setName("Define");
		addAlias("define");
	}
	
	@Override
	protected String format()
	{
		return super.format() + " [query]";
	}
	
	private static String define(String word)
	{
		try
		{
			List<Definition> definitions = WordApi.definitions(word);
			List<Example> exs = WordApi.examples(word).getExamples();
			String def = "", partOfSpeech = "", example = "";
			if(definitions.isEmpty())
				def = "Could no find definition.";
			else
			{
				def = definitions.get(0).getText();
				partOfSpeech = definitions.get(0).getPartOfSpeech();
				if(exs.isEmpty())
					example = "[No Example Avaliable]";
				else
					example = exs.get(0).getText();
			}
			
			return  Colors.DARK_GREEN + partOfSpeech + Colors.BROWN +": "+ def + ". " + 
					Colors.OLIVE + example;
		} catch (KnickerException e)
		{
			e.printStackTrace();
		}
		return Colors.MAGENTA + "Unable to find definition.";
	}
}
