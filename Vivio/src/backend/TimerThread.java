package backend;

import org.pircbotx.Colors;

public class TimerThread extends Thread{
	
	//TODO force "active" boolean true on run, false on finish
	
	private Bot context;
	
	public TimerThread(String name) {
		super(name);
	}
	
	public String toString() {
		return getState() == State.RUNNABLE ? Colors.RED + this.getName() + Colors.NORMAL : this.getName();
	}

	/**
	 * @return the context
	 */
	public Bot getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Bot context) {
		this.context = context;
	}
	
}
