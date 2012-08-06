/*
 * @author Kyle Kemp
 */
package backend;

import org.pircbotx.Colors;

// TODO: Auto-generated Javadoc
/**
 * The Class TimerThread.
 */
public class TimerThread extends Thread {

	// TODO force "active" boolean true on run, false on finish

	/** The context. */
	private Bot context;

	/**
	 * Instantiates a new timer thread.
	 * 
	 * @param name
	 *            the name
	 */
	public TimerThread(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#toString()
	 */
	public String toString() {
		return getState() == State.RUNNABLE ? Colors.RED + this.getName()
				+ Colors.NORMAL : this.getName();
	}

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public Bot getContext() {
		return context;
	}

	/**
	 * Sets the context.
	 * 
	 * @param context
	 *            the context to set
	 */
	public void setContext(Bot context) {
		this.context = context;
	}

}
