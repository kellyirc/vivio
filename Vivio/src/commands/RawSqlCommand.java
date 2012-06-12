/*
 * @author Kyle Kemp
 * @description This allows a user to send a raw query to the bot to get results, mostly used for debugging.
 * @basecmd sql
 * @category core
 */
package commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;



import backend.Bot;
import backend.Database;
import backend.Util;

public class RawSqlCommand extends Command{

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if(!Util.hasArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}

		String root = message.split(" ")[0];
		int select = 0;
		if(root.length() > 4) 
			select = Integer.parseInt(root.substring(4));
		
		String sql = message.substring(message.indexOf(" "));
		List<HashMap<String,Object>> data = null;
		try {
			if(sql.toLowerCase().contains("select"))
				data = Database.select(sql, select);
			else
				Database.execRaw(sql);
		} catch (SQLException e) {
			passMessage(bot, chan, user, e.getMessage());
			return;
		}
		
		if(data==null) {
			passMessage(bot, chan, user, "Executed query successfully.");
		} else if(data.size() == 0) {
			passMessage(bot, chan, user, "No returned rows.");
		} else {
			for(HashMap<String, Object> row : data) {
				passMessage(bot, chan, user, row.toString());
			}
		}
		
	}
	
	protected String format() {
		return super.format() + " [sql-command]";
	}
	
	@Override
	protected void initialize() {
		this.setAccessLevel(LEVEL_OWNER);
		this.setHelpText("Execute a raw SQL command. Oooooh, dangerous!");
		this.setName("SQL");
		addAlias("sql");
		setUsableInPM(true);
	}

	public void setActive(boolean active) {return;}
}
