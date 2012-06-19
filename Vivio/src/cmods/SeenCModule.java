/*
 * @author Kyle Kemp
 * @description This module allows a user to request the last available message of another user.
 * @basecmd seen
 * @category util
 */
package cmods;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import commands.Command;

import backend.Bot;
import backend.Util;

public class SeenCModule extends Command {

	private HashMap<String, Message> userMessages = new HashMap<>();
	
	private @Data @AllArgsConstructor class Message {
		private String message;
		private String channel;
		private long timestamp;
	}
	
	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception {
		super.onMessage(event);
		userMessages.put(event.getUser().getNick(), new Message(event.getMessage(), event.getChannel().getName(), System.currentTimeMillis()));
	}

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if(!Util.hasArgs(message, 2)) {
			invalidFormat(bot,chan,user);
			return;
		}
		
		String[] args = Util.getArgs(message,2);
		for(Channel c : bot.getChannels()) {
			for(User u : bot.getUsers(c)) {
				if(u.getNick().toLowerCase().equals(args[1].toLowerCase())) {
					passMessage(bot,chan,user,"You can find "+args[1]+" in "+c.getName()+" right now.");
					return;
				}
			}
		}
		
		for(String s : userMessages.keySet()) {
			if(s.toLowerCase().equals(args[1].toLowerCase())) {
				Message m = userMessages.get(s);
				passMessage(bot,chan,user,args[1]+" was last seen in "+m.getChannel()+" on "+new java.util.Date(m.getTimestamp())+" saying \""+m.getMessage()+"\"");
			}
		}
	}

	@Override
	protected void initialize() {
		setHelpText("See the last time someone said something in this channel.");
		addAlias("seen");
		setName("Seen");	
	}

	@Override
	protected String format() {
		return super.format()+ " {user}";
	}

}
