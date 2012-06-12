/*
 * @author Kyle Kemp
 */
package backend;

import java.util.HashSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;

public class TimerBackend extends ScheduledThreadPoolExecutor {
	
	@Getter private HashSet<TimerThread> threads = new HashSet<>();

	public TimerBackend(int corePoolSize) {
		super(corePoolSize);
	}
	
	public ScheduledFuture<?> scheduleTask(TimerThread command, long delayInSeconds) {
		for(TimerThread t : threads) {
			if(t.getClass().equals(command.getClass())) return null;
		}
		if(threads.add(command))
			return scheduleWithFixedDelay(command, 0, delayInSeconds, TimeUnit.SECONDS);
		return null;
	}
	
}
