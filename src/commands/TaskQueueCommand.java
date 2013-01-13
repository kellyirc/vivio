/*
 * @author Kyle Kemp
 * @description This module allows a user to check what tasks the bot is running in the background.
 * @basecmd task-queue
 * @category core
 */
package commands;

import java.util.HashSet;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.TimerThread;

// TODO: Auto-generated Javadoc
/**
 * The Class TaskQueueCommand.
 */
public class TaskQueueCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(final Bot bot, final Channel chan, final User user,
			String message) {
		passMessage(bot, chan, user, buildQueue(bot.getTimerThreads()));

		/*
		 * The following is an example of how to add a task to the bot.
		 * bot.scheduleTask(new TimerThread("TestThread") {
		 * 
		 * @Override public void run() { passMessage(bot, chan, user,
		 * "This goes off every 10 seconds!"); try { sleep(10000); } catch
		 * (InterruptedException e) { e.printStackTrace(); }
		 * 
		 * }}, 10);
		 */
	}

	/**
	 * Builds the queue.
	 * 
	 * @param queue
	 *            the queue
	 * @return the string
	 */
	private String buildQueue(HashSet<TimerThread> queue) {
		if (queue.size() == 0)
			return "There are no background tasks running.";
		String s = "Running threads:";
		for (Runnable t : queue) {
			s += " " + t;
		}
		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " {queue}";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		setName("TaskQueue");
		addAlias("task");
		addAlias("task-queue");
		setHelpText("Get information relating to tasks running in the background.");
		setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

}
