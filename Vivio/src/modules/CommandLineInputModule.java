package modules;

import java.util.Scanner;

import backend.Bot;

public class CommandLineInputModule extends Module {

	@Override
	protected void initialize() {
		setName("CommandLine");
		setHelpText("Take in commands from the command line. Yay, server administration!");
		setPriorityLevel(PRIORITY_MODULE);
		
		new InputThread("Command Line").start();
	}
	
	private static class InputThread extends Thread {

		private Scanner scan = new Scanner(System.in);
		
		public InputThread(String string) {
			super(string);
		}
		
		@Override
		public void run() {
			while(true) {
            	String s = scan.nextLine();
            	if(s.length()<1){continue;}
            	for(Bot bot : Bot.getBots())
            		if(bot.isParsesCmd()) 
            			bot.checkCommands(null, s, null, true);
			}
		}
		
	}

}
