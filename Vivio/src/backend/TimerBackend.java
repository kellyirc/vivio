package backend;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;

public class TimerBackend extends ScheduledThreadPoolExecutor {
	
	@Getter private ArrayList<TimerThread> threads = new ArrayList<>();

	public TimerBackend(int corePoolSize) {
		super(corePoolSize);
	}
	
	public ScheduledFuture<?> scheduleTask(TimerThread command, long delay) {
		threads.add(command);
		return scheduleWithFixedDelay(command, 0, delay, TimeUnit.SECONDS);
	}
	
}
