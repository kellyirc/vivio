package commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;

import database.Database;

import shared.Util;

import bot.Bot;

public class RawSqlCommand extends Command{

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if(!Util.checkArgs(message, 2)) {
			passMessage(bot, chan, user, "Invalid format: " + format());
			return;
		}
		
		String sql = message.substring(message.indexOf(" "));
		List<HashMap<String,Object>> data = null;
		try {
			if(sql.toLowerCase().contains("select"))
				data = Database.select(sql);
			else
				Database.execRaw(sql);
		} catch (SQLException e) {
			passMessage(bot, chan, user, e.getMessage());
		}
		
		if(data==null) {
			passMessage(bot, chan, user, "Executed query successfully.");
		} else {
			passMessage(bot, chan, user, data.toString());
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
		getAliases().add("sql");
	}

}
