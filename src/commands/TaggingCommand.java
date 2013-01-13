/*
 * @author Kyle Kemp
 * @description This module allows users to 'tag' links and browse them, a la a mini reddit or stumbleupon.
 * @basecmd tag
 * @category misc
 */
package commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

import backend.Bot;
import backend.Database;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class TaggingCommand.
 */
public class TaggingCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (Util.hasArgs(message, 3)) {
			String[] args = Util.getArgs(message, 3);
			String[] tags;

			if (!Util.hasLink(args[1])) {
				passMessage(bot, chan, user, "That isn't a link!");
				return;
			}

			if (args[2].contains(",")) {
				tags = args[2].split(",");
			} else {
				tags = new String[] { args[2] };
			}

			for (String s : tags) {
				try {
					if (Database.hasRow("select * from "
							+ getFormattedTableName() + " where url='"
							+ args[1] + "' and tag='" + s + "'")) {
						passMessage(bot, chan, user,
								"That url has already been recorded with tag "
										+ s + "!");
						continue;
					}
					Database.insert(getFormattedTableName(), "tag, url", "'"
							+ s + "','" + args[1] + "'");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			passMessage(bot, chan, user, "Successfully tagged as " + args[2]);

		} else if (Util.hasArgs(message, 2)) {
			// TODO view tags, how many entries are in each tag
			// String[] args = Util.getArgs(message, 2);

		} else {
			invalidFormat(bot, chan, user);
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(java.lang.String, backend.Bot,
	 * org.pircbotx.Channel, org.pircbotx.User, java.lang.String)
	 */
	public void execute(String alias, Bot bot, Channel chan, User user,
			String message) {
		if (alias.equals("tag")) {
			execute(bot, chan, user, message);
			return;
		} else if (alias.equals("tag-change")) {
			tagChange(bot, chan, user, message);
		} else if (alias.equals("tag-browse")) {
			tagBrowse(bot, chan, user, message);
		} else if (alias.equals("tags")) {
			// TODO !tags top 10
			// TODO !tags (lists all that have more than one entry)
		}
	}

	/**
	 * Tag browse.
	 * 
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param user
	 *            the user
	 * @param message
	 *            the message
	 */
	private void tagBrowse(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 2);
		List<HashMap<String, Object>> results = null;
		if (args[1].trim().equals("%")) {
			passMessage(bot, chan, user, "Nice try!");
			return;
		}
		try {
			if (args[1].contains("%"))
				results = Database.select("select * from "
						+ getFormattedTableName() + " where tag like '"
						+ args[1] + "'");
			else
				results = Database.select("select * from "
						+ getFormattedTableName() + " where tag = '" + args[1]
						+ "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (results == null) {
			passMessage(bot, chan, user, "No results for " + args[1]);
			return;
		}
		for (HashMap<String, Object> row : results) {
			String url = (String) row.get("URL");
			passMessage(bot, chan, user, backend.Util.parseLink(url) + " ("
					+ url + ")" + " [" + Colors.BOLD
					+ row.get("TAG").toString().trim() + Colors.NORMAL + "]");
		}
	}

	/**
	 * Tag change.
	 * 
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param user
	 *            the user
	 * @param message
	 *            the message
	 */
	private void tagChange(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 3)) {
			invalidFormat(bot, chan, user);
			return;
		}
		String[] args = Util.getArgs(message, 3);
		try {
			Database.execRaw("update " + getFormattedTableName() + " set tag='"
					+ args[2] + "' where tag='" + args[1] + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		passMessage(bot, chan, user, "Changed all " + args[1] + " to "
				+ args[2]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		setName("Tagging");
		setHelpText("Tag a link!");
		addAlias("tag");
		addAlias("tags");
		addAlias("tag-change");
		addAlias("tag-browse");
		setAccessLevel(LEVEL_ELEVATED);
		setTableName("tags");
		setUsableInPM(true);
		try {
			Database.createTable(getFormattedTableName(),
					"url VARCHAR(300), tag CHAR(30)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format()
				+ " [link | tag-to-view] [tag{,tag2{,tag3{,...}}}]";
	}

}
