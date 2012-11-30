package cmods;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.pircbotx.Channel;
import org.pircbotx.DccChat;
import org.pircbotx.User;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;

import commands.Command;

import console.Console;
import console.EchoConsole;
import console.JSConsole;
import console.RubyConsole;

public class DCCConsoleCModule extends Command
{

	private static final String consoleList = "echo, javascript, ruby";
	@Override
	protected void initialize()
	{
		setHelpText("To start a console session, use the command or initiate a DCC CHAT with me! Currently available consoles: "+consoleList+".");
		setName("DCCConsole");
		this.addAlias("console");
	}
	
	@Override
	protected String format()
	{
		return super.format() + " [console-name]";
	}
	
	@Override
	public void onIncomingChatRequest(IncomingChatRequestEvent<Bot> e)
			throws Exception
	{
		super.onIncomingChatRequest(e);
		DccChat chat = e.getChat();
		System.out.println("Received DCC Chat from "+chat.getAddress()+" "+chat.getSocket().getPort());
		chat.accept();
		chat.sendLine("Welcome to the console module! To start a console session, type your desired console. Currently supported consoles: "+consoleList+".");
		String line = chat.readLine();
		if(line == null)
			return;
		Console console = pickConsole(line);
		
		console.setDccChat(chat);
		startThread(console,chat);
	}

	private void startThread(final Console console, final DccChat chat)
	{
		Thread thread = new Thread() {
			@Override
			public void run()
			{
				super.run();
				 String line;
			        try
					{
						while ((line = chat.readLine()) != null) {
							if(line == null)
								break;
					        console.onLineReceived(line);
						}
				        chat.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
			}
		};
		thread.start();
	}

	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
        
        //Attempt to get a DCC chat with the user
        DccChat chat;
        Console console = null;
        String[] cmd = message.split(" ");
        if(cmd.length>=2)
        {
        	console = pickConsole(cmd[1]);
        }
        else
        {
        	this.invalidFormat(bot, chan, user);
        	return;
        }
        if(console == null)
        {
        	 passMessage(bot, chan, user, "Sorry, there are no consoles by that name");
        	 return;
        }
		try
		{
			System.out.println("Sending DCC CHAT request from "+bot.getDccInetAddress()+" "+bot.getDccPorts());
			chat = bot.dccSendChatRequest(user, 30000);
			 if(chat == null)
		        	return;
	        //We're now connected to the user
	        
	        //Create a console listener
//	        Console console = new EchoConsole();
	        console.setDccChat(chat);
	        String line;
	        while ((line = chat.readLine()) != null) {
	                console.onLineReceived(line);
	        }
	        chat.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
       
	}
	
	public Console pickConsole(String name)
	{
		switch(name.toLowerCase())
		{
			case "ruby":
				return new RubyConsole();
			case "javascript":
				return new JSConsole();
			case "echo":
				return new EchoConsole();
			default:
				return null;
		}
	}
}
