package console;

import java.io.IOException;

import org.pircbotx.DccChat;

public class EchoConsole extends Console
{

	public void initialize()
	{
		try
		{
			sendLine("Starting Echo Console.");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onLineReceived(String line)
	{
		try
		{
			sendLine(line);
		} catch (IOException e)
		{
			e.printStackTrace();
			close();
		}
	}
	
}
