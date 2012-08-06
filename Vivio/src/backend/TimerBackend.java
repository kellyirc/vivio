/*
 * @author Kyle Kemp
 */
package backend;

import java.util.HashSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;

// TODO: Auto-generated Javadoc
/**
 * The Class TimerBackend.
 */
public class TimerBackend extends ScheduledThreadPoolExecutor {

	/**
	 * Gets the threads.
	 * 
	 * @return the threads
	 */
	@Getter
	private HashSet<TimerThread> threads = new HashSet<>();

	/**
	 * Instantiates a new timer backend.
	 * 
	 * @param corePoolSize
	 *            the core pool size
	 */
	public TimerBackend(int corePoolSize) {
		super(corePoolSize);
	}

	/**
	 * Schedule task.
	 * 
	 * @param command
	 *            the command
	 * @param delayInSeconds
	 *            the delay in seconds
	 * @return the scheduled future
	 */
	public ScheduledFuture<?> scheduleTask(TimerThread command,
			long delayInSeconds) {
		return scheduleTask(command, delayInSeconds, TimeUnit.SECONDS);
	}

	/**
	 * Schedule task.
	 * 
	 * @param command
	 *            the command
	 * @param delayInSeconds
	 *            the delay in seconds
	 * @param timeUnit
	 *            the time unit
	 * @return the scheduled future
	 */
	public ScheduledFuture<?> scheduleTask(TimerThread command,
			long delayInSeconds, TimeUnit timeUnit) {
		for (TimerThread t : threads) {
			if (t.getClass().equals(command.getClass()))
				return null;
		}
		if (threads.add(command))
			return scheduleWithFixedDelay(command, 0, delayInSeconds, timeUnit);
		return null;
	}
}
