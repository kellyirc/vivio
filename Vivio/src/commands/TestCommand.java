package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import bot.Bot;

public class TestCommand extends Command{
	
	@Override
	protected void initialize() {
		getAliases().add("test");
		setName("Test");
	}

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		passMessage(bot, chan, user, "Hey, you tested.");
	}
	
	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events.MessageEvent)
	 */
	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception {
		event.getBot().sendMessage(event.getChannel(), event.getMessage());
		super.onMessage(event);
	}


}
