package commands;

import java.util.HashSet;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.TimerThread;
import backend.Util;

public class TaskQueueCommand extends Command {
	
	@Override
	public void execute(final Bot bot, final Channel chan, final User user, String message) {
		if(Util.hasArgs(message, 2)) {
			String[] args = Util.getArgs(message, 2);
			switch(args[1]) {
			case "queue":
				passMessage(bot, chan, user, buildQueue(bot.getTimerThreads()));
				break;
			}
			return;
		}
		
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
		addAlias("tasks");
		setHelpText("Get information relating to tasks running in the background.");
		setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

}
