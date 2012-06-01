package commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Database;
import backend.Util;

public class TaggingCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if(Util.hasArgs(message, 3)) {
			String[] args = Util.getArgs(message, 3);
			String[] tags;
						
			if(args[2].contains(",")) {
				tags = args[2].split(",");
			} else {
				tags = new String[] { args[2] };
			}
						
			for(String s : tags) {
				try {
					if(Database.hasRow("select * from "+getFormattedTableName()+" where url='"+ args[1]+"' and tag='"+s+"'")) {
						passMessage(bot, chan, user, "That url has already been recorded with tag "+s+"!");
						continue;
					}
					Database.insert(getFormattedTableName(), "tag, url", "'"+s+"','"+args[1]+"'");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			passMessage(bot, chan, user, "Successfully tagged as "+args[2]);
			
		} else if(Util.hasArgs(message, 2)) {
			//TODO view tags, how many entries are in each tag
			//String[] args = Util.getArgs(message, 2);
			
			
		} else {
			invalidFormat(bot, chan, user);
			return;
		}
	}

	public void execute(String alias, Bot bot, Channel chan, User user, String message) {
		if(alias.equals("tag")) {
			execute(bot, chan, user, message);
			return;
		}
		if(alias.equals("tag-change")) {
			if(!Util.hasArgs(message, 3)) {
				invalidFormat(bot, chan, user);
				return;
			}
			String[] args = Util.getArgs(message, 3);
			try {
				Database.execRaw("update "+getFormattedTableName()+" set tag='"+args[2]+"' where tag='"+args[1]+"'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			passMessage(bot, chan, user, "Changed all "+args[1]+" to "+args[2]);
		}
		if(alias.equals("tag-browse")) {
			if(!Util.hasArgs(message, 2)) {
				invalidFormat(bot, chan, user);
				return;
			}
			String[] args = Util.getArgs(message, 2);
			List<HashMap<String, Object>> results = null;
			try {
				if(args[1].contains("%")) 
					results = Database.select("select * from "+getFormattedTableName()+" where tag like '"+args[1]+"'");
				else
					results = Database.select("select * from "+getFormattedTableName()+" where tag = '"+args[1]+"'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(results == null) {
				passMessage(bot, chan, user, "No results for "+args[1]);
				return;
			}
			for(HashMap<String, Object> row : results) {
				passMessage(bot, chan, user, row.get("URL") + " [" + row.get("TAG").toString().trim() + "]");
			}
		}
	}

	@Override
	protected void initialize() {
		setName("Tagging");
		setHelpText("Tag a link!");
		addAlias("tag");
		addAlias("tag-change");
		addAlias("tag-browse");
		setAccessLevel(LEVEL_ELEVATED);
		setTableName("tags");
		try {
			Database.createTable(getFormattedTableName(), "url VARCHAR(300), tag CHAR(30)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected String format() {
		return super.format() + " [link | tag-to-view] [tag{,tag2{,tag3{,...}}}]";
	}

}
