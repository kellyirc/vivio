/*
 * @author Kyle Kemp
 */
package backend;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * The Class Initializer.
 */
public class Initializer {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			parseCommands(args);
		} else {
			new Bot();
		}
	}

	// TODO server pass
	/**
	 * Parses the commands.
	 * 
	 * @param args
	 *            the args
	 */
	public static void parseCommands(String[] args) {
		String server = Bot.DEFAULT_SERVER;
		String password = "";
		String channel = null;
		String nickservPass = "";
		String nickname = Bot.DEFAULT_NICKNAME;
		int port = Bot.DEFAULT_PORT;
		boolean ssl = false;
		InetAddress dccIP = null;
		ArrayList<Integer> dccPorts = new ArrayList<>();
		int maxMessageLength = Bot.DEFAULT_MAX_MESSAGE_LENGTH;

		for (int i = 0; i < args.length; i++) {
			if ((args[i].equals("-s") || args[i].equals("--server"))
					&& args.length >= i + 1) {
				server = args[i + 1];
			}
			if ((args[i].equals("-p") || args[i].equals("--port"))
					&& args.length >= i + 1) {
				port = Integer.parseInt(args[i + 1]);
			}
			if (args[i].equals("-pass") && args.length >= i + 1) {
				password = args[i + 1];
			}
			if (args[i].equals("--ssl") || args[i].equals("--use-ssl")) {
				ssl = true;
			}
			if (args[i].equals("-o") || args[i].equals("--owner")
					&& args.length >= i + 1) {
				Bot.addOwner(args[i + 1]);
			}
			if (args[i].equals("-c") || args[i].equals("--channel")
					&& args.length >= i + 1) {
				channel = args[i + 1];
			}
			if (args[i].equals("-n") || args[i].equals("--nick")
					&& args.length >= i + 1) {
				nickname = args[i + 1];
			}
			if (args[i].equals("-nickserv") && args.length >= i + 1) {
				nickservPass = args[i + 1];
			}
			if (args[i].equals("-i") || args[i].equals("--dcc-ip")
					&& args.length >= i + 1) {
				try
				{
					dccIP = InetAddress.getByName(args[i + 1]);
				} catch (UnknownHostException e)
				{
					e.printStackTrace();
				}
			}
			if (args[i].equals("-d") || args[i].equals("--dcc-port") || args[i].equals("--dcc-ports")
					&& args.length >= i + 1) {
				for(String arg:args[i + 1].split(","))
				{
					String[] range = arg.split("-");
					if(range.length==1)
						dccPorts.add(Integer.parseInt(arg));
					else
					{
						int begin = Integer.parseInt(range[0]);
						int end = Integer.parseInt(range[1]);
						for(int p = begin; p <= end; p++)
							dccPorts.add(p);
					}
				}
				
			}
			if (args[i].equals("--max-message-length") && args.length >= i + 1) {
				maxMessageLength = Integer.parseInt(args[i+1], 10);
			}
		}
		Bot b = null;
//		System.out.println(server+" "+port+" "+ssl+" "+nickname+" "+password);
		b = new Bot(server, port, ssl, nickname, password);
		if(dccIP != null)
			b.setDccInetAddress(dccIP);
		else
			b.setDccInetAddress(Util.getPublicIP());
		b.setMaxMessageLength(maxMessageLength);
		
		b.getDccPorts().addAll(dccPorts);
		System.out.println(b.getDccPorts());
		if (channel != null) {
			b.joinChannel(channel);
			b.joinChannel(channel + "-logs");
			b.joinChannel(channel + "-feeds");
		}
		if (nickservPass != null)
			b.identify(nickservPass);
		
		return;
	}

}
