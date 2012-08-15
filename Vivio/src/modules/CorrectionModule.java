package modules;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;

// TODO: Auto-generated Javadoc
/**
 * The Class CorrectionModule.
 */
public class CorrectionModule extends Module
{

	/** The last messages. */
	HashMap<Channel, String> lastMessages;

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
		setHelpText("Made a typo? Use regex! Format: s/regex/replacement");
		lastMessages = new HashMap<>();
		pattern = Pattern.compile("s/(.+)/(.+)");
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
		// // check for replace command
		// Matcher matcher = pattern.matcher(e.getMessage());
		// if (matcher.find() && lastMessages.containsKey(e.getChannel())) {
		// String replaced = lastMessages.get(e.getChannel()).replaceAll(
		// matcher.group(1), matcher.group(2));
		// if (!replaced.equals(lastMessages.get(e.getChannel()))) // msg only
		// // if
		// // changed
		// {
		// passMessage(e.getBot(), e.getChannel(), e.getUser(), replaced);
		// lastMessages.put(e.getChannel(), replaced);
		// }
		// } else
		// // update last Message
		// lastMessages.put(e.getChannel(), e.getMessage());

		String msg = e.getMessage();
		if (msg.startsWith("s/"))
		{
			msg = msg.replace("\\/", ((char) 26) + "");
			String[] split = msg.split("/");
			System.out.println(msg);
			String replaced = lastMessages.get(e.getChannel());
			for(int k=1;k<split.length-1;k+=2)
			{
				String regex = split[k].replace(((char) 26) + "", "/");
				String toReplace = split[k+1].replace(((char) 26) + "", "/");
				replaced = replaced.replaceAll(
						regex, toReplace);
			}
			if (!replaced.equals(lastMessages.get(e.getChannel()))) // msg only if changed
			{
				passMessage(e.getBot(), e.getChannel(), e.getUser(), replaced);
				lastMessages.put(e.getChannel(), replaced);
			}
		} else
			lastMessages.put(e.getChannel(), e.getMessage());
	}

}
