package modules;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;

public class CorrectionModule extends Module
{
	HashMap<Channel, String> lastMessages;
	Pattern pattern;
	@Override
	protected void initialize()
	{
		setPriorityLevel(PRIORITY_MODULE);
		setName("Correction");
		setHelpText("Made a typo? Use regex! Format: s/regex/replacement/");
		lastMessages = new HashMap<>();
		pattern = Pattern.compile("s/(.+)/(.+)/?");
	}
	
	
	
	@Override
	public void onMessage(MessageEvent<Bot> e) throws Exception
	{
		super.onMessage(e);
		//check for replace command
			Matcher matcher = pattern.matcher(e.getMessage());
			if(matcher.find() && lastMessages.containsKey(e.getChannel()))
			{
				String replaced = lastMessages.get(e.getChannel()).replaceAll(matcher.group(1), matcher.group(2));
				passMessage(e.getBot(), e.getChannel(), e.getUser(), replaced);
			}
		else //update last Message
			lastMessages.put(e.getChannel(), e.getMessage());
	}

}
