package modules;

import java.io.IOException;

import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.events.DisconnectEvent;

import backend.Bot;

public class ReconnectModule extends Module {

	@Override
	protected void initialize() {
		setName("Reconnect");
		setHelpText("My only purpose in life is to make sure the bot stays connected.");
		setPriorityLevel(PRIORITY_MODULE);
	}
	
	public void onDisconnect(DisconnectEvent<Bot> event) {
		int attempts = 0;
		
		do {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
			System.out.println("Attempting to reconnect (try #" + (++attempts) + ")");
			
			try {
				if (event.getBot().isUsesSSL())
					event.getBot().connect(
							event.getBot().getServer(), event.getBot().getPort(), event.getBot().getServerPassword(),
							new UtilSSLSocketFactory().trustAllCertificates());
				else
					event.getBot().connect(
							event.getBot().getServer(), event.getBot().getPort(), event.getBot().getServerPassword());
			} catch (IOException | IrcException e) {
				System.err.println("Unable to reconnect");
				e.printStackTrace();
			}
		} while (!event.getBot().isConnected() && attempts < 99);
		
		if(!event.getBot().isConnected()) {
			System.err.println("Unable to connect to the same server, it is unlikely I'll ever be able to reconnect");
		}
		else {
			String identPass = event.getBot().getIdentifyPass();
			if(identPass != null && identPass.length() > 0) event.getBot().identify(identPass);
		}
	}
}
