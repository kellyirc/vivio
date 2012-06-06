package commands;

import java.sql.SQLException;
import java.util.HashMap;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import backend.Bot;
import backend.Database;
import backend.Util;

public class FactoidCModule extends Command {
	
	private boolean allowDuplicates = true;
	private HashMap<String, Object> lastFactoid;

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		
	}

	@Override
	protected void initialize() {
		setName("Factoids");
		setHelpText("I can keep track of random things with this!");		
		setTableName("factoids");

		try {
			Database.createTable(
					this.getFormattedTableName(),
					"server char(20), channel char(30), user_nick char(25), precondition varchar(200), action_type char(20), text varchar(400), time timestamp not null default current timestamp");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception {
		super.onMessage(event);
		
		checkForPreviousFactoidMessage(event);
		
		if(!checkForMatchesForDatabase(event))
			checkForMatchesForSpeaking(event);
	}

	private void checkForPreviousFactoidMessage(MessageEvent<Bot> event) {
		if(!event.getMessage().toLowerCase().contains("what was that?")) return;
		if(lastFactoid == null) return;
		
		passMessage(event.getBot(), event.getChannel(), event.getUser(), "FACTOID #"+lastFactoid.get("ID") + " submitted by "+lastFactoid.get("USER_NICK").toString().trim() + ": "+lastFactoid.get("PRECONDITION")+ " " + lastFactoid.get("ACTION_TYPE").toString().trim() + " " + lastFactoid.get("TEXT"));
		
		lastFactoid = null;
		
	}

	private void checkForMatchesForSpeaking(MessageEvent<Bot> event) {
		for(String s : event.getMessage().split(" ")) {
			String query = "select * from "+this.getFormattedTableName()+" where lower(precondition) like "+Database.getEnclosedString(s.toLowerCase());
			HashMap<String, Object> rand = null;
			try {
				if(Database.hasRow(query)){
					rand = Database.getRandomRow(Database.select(query));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(rand == null) continue;
			
			String message = rand.get("PRECONDITION") + " " + rand.get("ACTION_TYPE").toString().trim() + " " + rand.get("TEXT");
			
			passMessage(event.getBot(), event.getChannel(), event.getUser(), event.getUser().getNick()+", "+message);
			
			lastFactoid = rand;
		}
	}

	private boolean checkForMatchesForDatabase(MessageEvent<Bot> event) throws SQLException {
		boolean match=false;
		String message = event.getMessage();
		if(Util.hasLink(message)) return false;
		if(message.matches(".+\\<(.*?)\\>.+")) {
			String[] args = new String[3];
			
			args[0] = message.substring(0, message.indexOf("<")).trim();
			if(!allowDuplicates && Database.hasRow("select * from "+this.getFormattedTableName()+" where precondition='"+args[0]+"'")) return false;
			args[1] = message.substring(message.indexOf("<"), message.indexOf(">")+1).trim();
			args[2] = message.substring(message.indexOf(">")+1).trim();
			
			Database.insert(this.getFormattedTableName(), "server, channel, user_nick, precondition, action_type, text", 
					Database.getEnclosedString(event.getBot().getServer())+","+
							Database.getEnclosedString(event.getChannel().getName())+","+
							Database.getEnclosedString(event.getUser().getNick())+ ","+
							Database.getEnclosedString(args[0])+","+
							Database.getEnclosedString(args[1])+","+
							Database.getEnclosedString(args[2]));
			match=true;
			
		} else if(Util.hasArgs(message, 2, " is ") && message.split(" ")[1].equals("is")) {
			String[] args = Util.getArgs(message, 2, " is ");
			if(!allowDuplicates && Database.hasRow("select * from "+this.getFormattedTableName()+" where precondition="+Database.getEnclosedString(args[0]))) return false;
			Database.insert(this.getFormattedTableName(), "server, channel, user_nick, precondition, action_type, text", 
					Database.getEnclosedString(event.getBot().getServer())+","+
							Database.getEnclosedString(event.getChannel().getName())+","+
							Database.getEnclosedString(event.getUser().getNick())+ ","+
							Database.getEnclosedString(args[0])+","+
							Database.getEnclosedString("is")+","+
							Database.getEnclosedString(args[1]));
			match=true;
			
		} else if(Util.hasArgs(message, 2, " are ") && message.split(" ")[1].equals("are")) {
			String[] args = Util.getArgs(message, 2, " are ");
			if(!allowDuplicates && Database.hasRow("select * from "+this.getFormattedTableName()+" where precondition="+Database.getEnclosedString(args[0]))) return false;
			Database.insert(this.getFormattedTableName(), "server, channel, user_nick, precondition, action_type, text", 
					Database.getEnclosedString(event.getBot().getServer())+","+
							Database.getEnclosedString(event.getChannel().getName())+","+
							Database.getEnclosedString(event.getUser().getNick())+ ","+
							Database.getEnclosedString(args[0])+","+
							Database.getEnclosedString("are")+","+
							Database.getEnclosedString(args[1]));
			match=true;
			
		}
		if(match) {
			passMessage(event.getBot(), event.getChannel(), event.getUser(), "Okay, "+event.getUser().getNick()+".");
			return true;
		}
		return false;
	}

}
