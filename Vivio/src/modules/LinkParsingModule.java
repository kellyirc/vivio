package modules;


import org.pircbotx.hooks.events.MessageEvent;


import backend.Bot;
import backend.Util;

public class LinkParsingModule extends Module {
	
	@Override
	protected void initialize() {
		setPriorityLevel(PRIORITY_MODULE);
		setName("LinkParse");
		setHelpText("I parse your links so people can get a synopsis of them!");
	}

	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception {
		super.onMessage(event);
		
		String[] splitMessage = event.getMessage().split(" ");
		for(String s : splitMessage) {
			if(Util.hasLink(s)) {
				
				String title = Util.parseLink(s);
				
				if(title.equals("")) continue;
				
				passMessage(event.getBot(), event.getChannel(), event.getUser(), event.getUser().getNick()+"'s URL: "+title);
				
			}
		}
	}

}
