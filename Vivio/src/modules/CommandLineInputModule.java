/*
 * @author Kyle Kemp
 * @description This module allows the server host to control the bot from the command line.
 * @category core
 */
package modules;

import java.util.Scanner;

import backend.Bot;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandLineInputModule.
 */
public class CommandLineInputModule extends Module {

	/** The input thread. */
	static InputThread inputThread;

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		setName("CommandLine");
		setHelpText("Take in commands from the command line. Yay, server administration!");
		setPriorityLevel(PRIORITY_MODULE);

		if (inputThread != null) {
			inputThread = new InputThread("Command Line");
			inputThread.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#setActive(boolean)
	 */
	public void setActive(boolean a) {
		return;
	}

	/**
	 * The Class InputThread.
	 */
	private static class InputThread extends Thread {

		/** The scan. */
		private Scanner scan = new Scanner(System.in);

		/**
		 * Instantiates a new input thread.
		 * 
		 * @param string
		 *            the string
		 */
		public InputThread(String string) {
			super(string);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			while (true) {
				String s = scan.nextLine();
				if (s.length() < 1) {
					continue;
				}
				for (Bot bot : Bot.getBots())
					if (bot.isParsesCmd())
						bot.checkCommands(null, s, null, true);
			}
		}

	}

}
