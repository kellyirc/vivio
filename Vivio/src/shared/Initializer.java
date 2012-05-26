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
	public static void parseCommands(String[] args) {
		String server = null;
		String password = null;
		String channel = null;
		String nickservPass = null;
		int port = -1;
		boolean ssl = false;
	 	
		for(int i=0; i<args.length; i++) {
			if((args[i].equals("-s") || args[i].equals("-server")) && args.length >= i+1) {
				server = args[i+1];
			}
			if((args[i].equals("-p") || args[i].equals("-port")) && args.length >= i+1) {
				port = Integer.parseInt(args[i+1]);
			}
			if(args[i].equals("-pass") && args.length >= i+1) {
				password = args[i+1];
			}
			if(args[i].equals("--ssl") || args[i].equals("--use-ssl")) {
				ssl = true;
			}
			if(args[i].equals("-o") || args[i].equals("-owner") && args.length >= i+1) {
				Bot.addOwner(args[i+1]);
			}
			if(args[i].equals("-c") || args[i].equals("-channel") && args.length >= i+1) {
				channel = args[i+1];
			}
			if(args[i].equals("-n") || args[i].equals("-nickserv") && args.length > i+1) {
				nickservPass = args[i+1];
			}
		}
		Bot b = null;
				
		if(server!=null) {
			if(port != -1) {
				if(ssl) {
					b = new Bot(server, port, ssl);
					if(channel!=null) b.joinChannel(channel);
					return;
				}
				
				b =new Bot(server, port);
				if(channel!=null) b.joinChannel(channel);
				return;
			}
			
			b =new Bot(server);
			if(channel!=null) b.joinChannel(channel);
			return;
		}
	}
	
}
