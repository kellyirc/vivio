package backend;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimerFrontEnd extends ScheduledThreadPoolExecutor {

	public TimerFrontEnd(int corePoolSize) {
		super(corePoolSize);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
			long initialDelay, long delay, TimeUnit unit) {
		return super.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}
	
	public ScheduledFuture<?> scheduleTask(Runnable command, long delay) {
		return scheduleWithFixedDelay(command, 0, delay, TimeUnit.SECONDS);
	}
	
}
