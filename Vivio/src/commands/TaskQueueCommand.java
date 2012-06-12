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

public class TaskQueueCommand extends Command {
	
	@Override
	public void execute(final Bot bot, final Channel chan, final User user, String message) {
		passMessage(bot, chan, user, buildQueue(bot.getTimerThreads()));
		
		/*
		The following is an example of how to add a task to the bot.
		bot.scheduleTask(new TimerThread("TestThread") {

			@Override
			public void run() {
				passMessage(bot, chan, user, "This goes off every 10 seconds!");
				try {
					sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}}, 10);*/
	}

	private String buildQueue(HashSet<TimerThread> queue) {
		if(queue.size() == 0) return "There are no background tasks running.";
		String s = "Running threads:";
		for(Runnable t : queue) {
			s += " " + t;
		}
		return s;
	}

	protected String format() {
		return super.format() + " {queue}";
	}
	
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
