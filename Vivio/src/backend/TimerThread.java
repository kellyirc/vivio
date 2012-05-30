package backend;

import org.pircbotx.Colors;

public class TimerThread extends Thread{
	
	public TimerThread(String name) {
		super(name);
	}
	
	public String toString() {
		return getState() == State.RUNNABLE ? Colors.RED + this.getName() + Colors.NORMAL : this.getName();
	}
	
}
