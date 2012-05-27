package backend;


public class Initializer {
	public static void main(String[] args) {
		if(args.length > 0) {
			parseCommands(args);
		} else {
			new Bot();
		}
	}
	
	//TODO server pass
	public static void parseCommands(String[] args) {
		String server = Bot.DEFAULT_SERVER;
		String password = "";
		String channel = null;
		String nickservPass = "";
		String nickname = Bot.DEFAULT_NICKNAME;
		int port = Bot.DEFAULT_PORT;
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
			if(args[i].equals("-n") || args[i].equals("-nickserv") && args.length >= i+1) {
				nickservPass = args[i+1];
			}
			if(args[i].equals("-nick") && args.length >= i+1) {
				nickname = args[i+1];
			}
		}
		Bot b = null;
				
		if(server!=null) {
			if(port != -1) {
				if(ssl) {
					b = new Bot(server, port, ssl, nickname);
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
		b = new Bot(server, port, ssl, nickname, password);
		if(channel!=null) b.joinChannel(channel);
		return;
	}
	
}
