package commands;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;


import backend.Bot;
import backend.Database;
import backend.Util;

public class LoggingCModule extends Command {

	@Override
	public void execute(final Bot bot, final Channel chan, final User user, String message) {
		if(chan == null) return;
		
		if(Util.hasArgs(message, 2)) {
			String[] args = Util.getArgs(message, 2);
			switch(args[1]) {
			case "quote":
				try {
					displayRandomQuote(bot, chan, user, getDataForChannel(chan));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case "link":
				try {
					displayRandomLink(bot, chan, user, getDataForChannel(chan));
				} catch (SQLException | IOException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
			return;
		}
		
		passMessage(bot, chan, user, "I will begin generating statistics for "+chan.getName()+ " now.");
		
		//TODO total domination statistic, when you are the name for every single statistic
		new Thread(new Runnable(){

			@Override
			public void run() {
				List<HashMap<String, Object>> returned = null;
				try {
					 returned = getDataForChannel(chan);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				if(returned == null) {
					passMessage(bot, chan, user, "I could not generate statistics for "+chan.getName());
					return;
				}
				passMessage(bot, chan, user, "I have "+returned.size()+" recorded messages from "+chan.getName()+".");
				
				displayRandomQuote(bot, chan, user, returned);
				try {
					displayRandomLink(bot, chan, user, returned);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				HashMap<String, Integer> httpcount = new HashMap<>();
				HashMap<String, Integer> cmdcount = new HashMap<>();
				
				//these are doubles so they don't have to be cast later
				double httpNum = 0;
				double cmdNum = 0;
				
				for(HashMap<String, Object> column : returned) {
					String message = column.get("MESSAGE").toString();
					String sender = column.get("USER_NAME").toString().trim();
					if(Util.hasLink(message)) {
						httpNum++;
						if(httpcount.containsKey(sender)) httpcount.put(sender, httpcount.get(sender)+1);
						else httpcount.put(sender, 1);
					} else if(message.startsWith("!")) {
						cmdNum++;
						if(cmdcount.containsKey(sender)) cmdcount.put(sender, cmdcount.get(sender)+1);
						else cmdcount.put(sender, 1);
					}
				}
				
				Map.Entry<String,Integer> maxHttp = getMax(httpcount);
				Map.Entry<String,Integer> maxCmd = getMax(cmdcount);
				
				DecimalFormat twoDForm = new DecimalFormat("#.##");
				//TODO most popular domain
				passMessage(bot, chan, user, "Total links posted: " + (int)httpNum + " ("+twoDForm.format((httpNum/returned.size())*100)+"% of all messages).");
				if(maxHttp!=null)passMessage(bot, chan, user, "Most links posted: "+maxHttp.getKey() + ", with "+maxHttp.getValue()+ " links ("+twoDForm.format((maxHttp.getValue()/httpNum)*100)+"% of all links)."  );
				
				passMessage(bot, chan, user, "Total commands posted: " + (int)cmdNum + " ("+twoDForm.format((cmdNum/returned.size())*100)+"% of all messages).");
				if(maxCmd!=null)passMessage(bot, chan, user, "Most commands used: "+maxCmd.getKey() + ", with "+maxCmd.getValue()+ " commands ("+twoDForm.format((maxCmd.getValue()/cmdNum)*100)+"% of all commands).");
				
				
			}}).start();
	}

	private Map.Entry<String,Integer> getMax(HashMap<String, Integer> httpcount) {
		Map.Entry<String, Integer> curMax = null;
		
		for(Map.Entry<String, Integer> entry : httpcount.entrySet()) {
			if(curMax == null || httpcount.get(entry.getKey()) > httpcount.get(curMax.getKey())) curMax = entry;
		}
		return curMax;
	}

	private void displayRandomQuote(final Bot bot, final Channel chan,
			final User user, List<HashMap<String, Object>> returned) {
			HashMap<String, Object> rand;
			String message;
			do {
				rand = Database.getRandomRow(returned);
				message = rand.get("MESSAGE").toString();
			} while(message.startsWith("!") || message.split(" ").length < 3 || message.length() < 10);
			passMessage(bot, chan, user, "Random quote: <"+((String)rand.get("USER_NAME")).trim() + "> " + message);
	}
	
	private void displayRandomLink(final Bot bot, final Channel chan,
			final User user, List<HashMap<String, Object>> returned) throws MalformedURLException, IOException {
			HashMap<String, Object> rand;
			String link;
			do {
				rand = Database.getRandomRow(returned);
				link = rand.get("MESSAGE").toString();
			} while(!Util.hasLink(link) || Util.parseLink(link).equals(""));
			passMessage(bot, chan, user, "Random link: <"+((String)rand.get("USER_NAME")).trim() + "> " + Util.extractLink(link) + " -- "+Util.parseLink(link));
	}
	
	private List<HashMap<String, Object>> getDataForChannel(
			final Channel chan) throws SQLException {
		List<HashMap<String,Object>> returned = Database.select("select * from "+getFormattedTableName()+" where channel="+Database.getEnclosedString(chan.getName()));
		return returned;
	}

	@Override
	protected void initialize() {
		addAlias("generate-stats");
		this.setAccessLevel(LEVEL_ELEVATED);
		this.setPriorityLevel(PRIORITY_MODULE);
		this.setHelpText("Wheeeee, retrieve those logs!");
		this.setName("LoggingStats");
		this.setTableName("logs"); 

		try {
			Database.createTable(
					this.getFormattedTableName(),
					"server char(20), channel char(30), user_name char(25), message varchar(600), time timestamp not null default current timestamp");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception {
		Database.insert(getFormattedTableName(),
				"server, channel, user_name, message", Database
						.getEnclosedString(event.getBot().getServer())
								+ ","
								+ Database.getEnclosedString(event.getChannel()
										.getName())
										+ ","
										+ Database.getEnclosedString(event
												.getUser().getNick())
										+ ","
										+ Database.getEnclosedString(event
												.getMessage())
												);
		super.onMessage(event);
	}

}
