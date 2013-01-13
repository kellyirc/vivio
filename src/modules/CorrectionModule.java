/*
 * @author Rahat Ahmed
 * 
 * @description This module emulates the behavior of Unix program sed, allowing
 * users to correct lines that have already been sent with regex.
 * 
 * @category utility
 */
package modules;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;
import backend.Database;
import cmods.LoggingCModule;

// TODO: Auto-generated Javadoc
/**
 * The Class CorrectionModule.
 */
public class CorrectionModule extends Module
{

	/** The last messages. */
//	HashMap<Channel, String> lastMessages;

	/** The pattern. */
	Pattern pattern;

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize()
	{
		setPriorityLevel(PRIORITY_MODULE);
		setName("Correction");
		setHelpText("Made a typo? Use regex! Format: s(n)/regex/replacement/regex/replacement...");
//		lastMessages = new HashMap<>();
		pattern = Pattern.compile("s\\d*/(.+)/(.+)");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events
	 * .MessageEvent)
	 */
	@Override
	public void onMessage(MessageEvent<Bot> e) throws Exception
	{
		super.onMessage(e);

		String msg = e.getMessage();
		if (msg.matches("s\\d*/(.+)/(.+)"))
		{
			msg = msg.replace("\\/", ((char) 26) + "");
			String[] split = msg.split("/");
			
			int n = 1;
			//try to get the number, else assume 1
			try { n = Integer.parseInt(split[0].substring(1)); }
			catch (NumberFormatException ex) { };
			
			List<HashMap<String,Object>> query = Database.select("SELECT message FROM LoggingCModule_logs WHERE event_type="+LoggingCModule.EventType.MESSAGE.ordinal()+" AND channel="+Database.getEnclosedString(e.getChannel().getName())+" ORDER BY id DESC",n);
			if(query.isEmpty())
				return;
			String original = (String) query.get(query.size()-1).get("MESSAGE");
			String replaced = original;
			for(int k=1;k<split.length-1;k+=2)
			{
				String regex = split[k].replace(((char) 26) + "", "/");
				String toReplace = split[k+1].replace(((char) 26) + "", "/");
				replaced = replaced.replaceAll(
						regex, toReplace);
			}
			if (!replaced.equals(original)) // msg only if changed
			{
				passMessage(e.getBot(), e.getChannel(), e.getUser(), replaced);
			}
		}
	}

}
