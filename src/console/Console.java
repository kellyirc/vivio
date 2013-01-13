package console;

import java.io.IOException;
import java.net.InetAddress;

import org.pircbotx.DccChat;
import org.pircbotx.User;

/**
 * An interface to allow communication by some interactive console and a user
 * via DCC CHAT.
 * @author Rahat
 *
 */
public abstract class Console
{
	private DccChat chat;
	
	/**
	 * Sets the DccChat instance to attach to this Console session. This should
	 * only be used when instantiating the Console in the backend. If you are 
	 * extending this class, you don't need to use this.
	 * @param chat The DccChat instance to attach to
	 */
	public void setDccChat(DccChat chat)
	{
		if(this.chat != null)
			throw new IllegalStateException("The DccChat was already set!");
		this.chat = chat;
		this.initialize();
	}
	
	/**
	 * Instantiates anything needed for the implementation of a Console
	 */
	public abstract void initialize();

	/**
	 * A listener method that is called every time a line is received from the
	 * user.
	 * @param line The line received from the user.
	 */
	public abstract void onLineReceived(String line);
	
//	/**
//	 * Returns the name of this console type. This name will be used when the user
//	 * chooses which console he wants.
//	 * @return The name of this console type.
//	 */
//	public abstract String getName();
	
	/**
	 * Sends a line to the user.
	 * @param line The line to send.
	 * @throws IOException
	 */
	protected void sendLine(String line) throws IOException
	{
		chat.sendLine(line);
	}

	/**
	 * Returns the User for this Console session.
	 * @return The User for this Console session.
	 */
	public User getUser()
	{
		return chat.getUser();
	}
	
	/**
	 * Returns the address for this Console session's DccChat.
	 * @return the address for this Console session's DccChat.
	 */
	public InetAddress getAddress()
	{
		return chat.getAddress();
	}
	
	/**
	 * Closes the DCC CHAT session attached to this Console. Consoles should not
	 * be used after they are closed.
	 */
	public void close()
	{
		try
		{
			chat.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
