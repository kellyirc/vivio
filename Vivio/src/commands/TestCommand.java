/*
 * @author Kyle Kemp
 * @description This module is a shell that does next to nothing.
 * @basecmd test
 * @category misc
 */
package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;

// TODO: Auto-generated Javadoc
/**
 * The Class TestCommand.
 */
public class TestCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		addAlias("test");
		setName("Test");
		this.setHelpText("This is just a test. This is only a test.");
		this.setActive(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		passMessage(bot, chan, user, "Hey, you tested.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events
	 * .MessageEvent)
	 */
	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception {
		event.getBot().sendMessage(event.getChannel(), event.getMessage());
		super.onMessage(event);
	}

}
