package shared;
import bot.Bot;


public class Initializer {
	public static void main(String[] args) {
		if(args.length > 0) {
			parseCommands(args);
		} else {
			new Bot();
		}
	}
	
	//TODO bot auth?
	//TODO server pass
	private static void parseCommands(String[] args) {
		String server = null;
		int port = -1;
		boolean ssl = false;
	 	
		for(int i=0; i<args.length; i++) {
			if((args[i].equals("-s") || args[i].equals("-server")) && args.length >= i+1) {
				server = args[i+1];
			}
			if((args[i].equals("-p") || args[i].equals("-port")) && args.length >= i+1) {
				port = Integer.parseInt(args[i+1]);
			}
			if(args[i].equals("--ssl") || args[i].equals("--use-ssl")) {
				ssl = true;
			}
			if(args[i].equals("-owner") || args[i].equals("-o") && args.length >= i+1) {
				Bot.addOwner(args[i+1]);
			}
		}
		
		if(server!=null) {
			if(port != -1) {
				if(ssl) {
					new Bot(server, port, ssl);
					return;
				}
				
				new Bot(server, port);
				return;
			}
			
			new Bot(server);
			return;
		}
	}
	
}
