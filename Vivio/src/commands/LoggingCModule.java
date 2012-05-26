package commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import database.Database;

import bot.Bot;

public class LoggingCModule extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		passMessage(bot, chan, user, "");
		try {
			List<HashMap<String,Object>> returned = Database.select("");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void initialize() {
		getAliases().add("generate-stats");
		this.setAccessLevel(LEVEL_OPERATOR);
		this.setPriorityLevel(PRIORITY_MODULE);
		this.setHelpText("Wheeeee, retrieve those logs!");
		this.setName("Logging");
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
												.getMessage().replaceAll("'", "''"))
												);
		super.onMessage(event);
	}

}
