/*
 * @author Kyle Kemp
 * @description This module allows a user to request the last available message of another user.
 * @basecmd seen
 * @category util
 */
package cmods;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;
import backend.Database;
import backend.Util;

import commands.Command;

public class SeenCModule extends Command {

//	private HashMap<String, Message> userMessages = new HashMap<>();
	
//	private @Data @AllArgsConstructor class Message {
//		private String message;
//		private String channel;
//		private long timestamp;
//	}
	
	@Override
	public void onMessage(MessageEvent<Bot> e) throws Exception {
		super.onMessage(e);
//		userMessages.put(event.getUser().getNick(), new Message(event.getMessage(), event.getChannel().getName(), System.currentTimeMillis()));
		List<HashMap<String,Object>> result = Database.select("SELECT * FROM "+getFormattedTableName()+" WHERE lower(nick)="+Database.getEnclosedString(e.getUser().getNick().toLowerCase()));
		if(result.isEmpty())
		{
		Database.insert(getFormattedTableName(), "nick, message, time, chan", 
						Database.getEnclosedString(e.getUser().getNick())+","+
						Database.getEnclosedString(e.getMessage())+","+
						Database.formatTimestamp(System.currentTimeMillis())+","+
						Database.getEnclosedString(e.getChannel().getName())
						);
		}
		else
		{
			Database.execRaw("UPDATE "+getFormattedTableName()+" SET message="+Database.getEnclosedString(e.getMessage())+
																	", time="+Database.formatTimestamp(System.currentTimeMillis())+
																	", chan="+Database.getEnclosedString(e.getChannel().getName()));
		}
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
		
//		for(String s : userMessages.keySet()) {
//			if(s.toLowerCase().equals(args[1].toLowerCase())) {
//				Message m = userMessages.get(s);
//				passMessage(bot,chan,user,args[1]+" was last seen in "+m.getChannel()+" on "+new java.util.Date(m.getTimestamp())+" saying \""+m.getMessage()+"\"");
//			}
//		}
		try {
			List<HashMap<String,Object>> result = Database.select("SELECT * FROM "+getFormattedTableName()+" WHERE lower(nick)="+args[1].toLowerCase());
			if(!result.isEmpty() && result.get(0).get("NICK").toString().toLowerCase().equals(args[1].toLowerCase()))
			{
				HashMap<String,Object> row = result.get(0);
				String resultNick = row.get("NICK").toString();
				String resultMessage = row.get("MESSAGE").toString();
				String resultChan = row.get("CHAN").toString();
				String resultTime = row.get("TIME").toString();
				passMessage(bot,chan,user,resultNick+" was last seen in "+resultChan+" on "+resultTime+" saying \""+resultMessage+"\"");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void initialize() {
		setHelpText("See the last time someone said something in this channel.");
		addAlias("seen");
		setName("Seen");	
		setTableName("seen");
		try {
			Database.createTable(getFormattedTableName(), "nick VARCHAR(30), message VARCHAR(600), time TIMESTAMP, chan VARCHAR(30)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String format() {
		return super.format()+ " {user}";
	}

}
