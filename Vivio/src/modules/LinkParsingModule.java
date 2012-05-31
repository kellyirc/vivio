package modules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;

public class LinkParsingModule extends Module {

	private boolean isBusy = false;
	
	@Override
	protected void initialize() {
		setPriorityLevel(PRIORITY_MODULE);
		setName("LinkParse");
		setHelpText("I parse your links so people can get a synopsis of them!");
	}

	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception {
		if(isBusy) return;
		super.onMessage(event);
		
		isBusy = true;
		
		String[] splitMessage = event.getMessage().split(" ");
		for(String s : splitMessage) {
			if(s.contains("://")) {
				String page = "";
				URL url = new URL(s);
				URLConnection con = url.openConnection();
				con.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.4) Gecko/20100611 Firefox/3.6.4");
				String inputLine;
				try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
					while((inputLine = in.readLine()) != null) {
						if(inputLine.contains("<title>") && inputLine.contains("</title>")) { 
							page = inputLine.substring(inputLine.indexOf("<title>"));
							break;
						} else if(inputLine.contains("<title>")) {
							page = inputLine.substring(inputLine.indexOf("<title>"));
						} else if(inputLine.contains("</title>")) {
							page += inputLine;
							break;
						}
					}
				}
				isBusy = false;
				
				if(page.equals("")) return;
				
				String title = page.substring(page.indexOf("<title>")+7, page.indexOf("</title>"));
				
				passMessage(event.getBot(), event.getChannel(), event.getUser(), event.getUser().getNick()+"'s URL: "+title);
				
			}
		}
	}

}
