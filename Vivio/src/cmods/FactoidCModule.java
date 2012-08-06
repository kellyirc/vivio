/*
 * @author Kyle Kemp
 * @description This module allows the bot to learn and speak about various topics.
 * @basecmd factoid
 * @category ai
 */
package cmods;

import java.sql.SQLException;
import java.util.HashMap;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import commands.Command;

import backend.Bot;
import backend.Database;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class FactoidCModule.
 */
public class FactoidCModule extends Command {

	/** The allow duplicates. */
	private boolean allowDuplicates = true;

	/** The last factoid. */
	private HashMap<String, Object> lastFactoid;

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 3)) {
			invalidFormat(bot, chan, user);
			return;
		}

		String[] args = Util.getArgs(message, 3);
		switch (args[1]) {
		// TODO support a word replacement syntax
		case "replace":
			break;
		// TODO remember the quote for a factoid for a person in the last 20 or
		// so lines
		case "remember":
			break;
		// TODO remove latest
		case "remove":
			try {
				Database.execRaw("delete from " + getFormattedTableName()
						+ " where id=" + args[2]);
			} catch (SQLException e) {
				passMessage(bot, chan, user, "That is not possible!");
			}
			passMessage(bot, chan, user,
					"Done successfully, quickly, and with prejudice!");
			break;
		default:
			invalidFormat(bot, chan, user);
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format() + " [remove | replace] {args}";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		setName("Factoids");
		setHelpText("I can keep track of random things with this!");
		setTableName("factoids");
		addAlias("factoid");
		setAccessLevel(LEVEL_OWNER);
		setActive(false);
		setUsableInPM(true);

		try {
			Database.createTable(
					this.getFormattedTableName(),
					"server char(20), channel char(30), user_nick char(25), precondition varchar(200), action_type char(20), text varchar(400), time timestamp not null default current timestamp");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events
	 * .MessageEvent)
	 */
	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception {
		super.onMessage(event);

		checkForPreviousFactoidMessage(event);

		if (!checkForMatchesForDatabase(event))
			checkForMatchesForSpeaking(event);
	}

	/**
	 * Check for previous factoid message.
	 * 
	 * @param event
	 *            the event
	 */
	private void checkForPreviousFactoidMessage(MessageEvent<Bot> event) {
		if (!event.getMessage().toLowerCase().contains("what was that?"))
			return;
		if (lastFactoid == null)
			return;

		passMessage(event.getBot(), event.getChannel(), event.getUser(),
				"FACTOID #" + lastFactoid.get("ID") + " submitted by "
						+ lastFactoid.get("USER_NICK").toString().trim() + ": "
						+ lastFactoid.get("PRECONDITION") + " "
						+ lastFactoid.get("ACTION_TYPE").toString().trim()
						+ " " + lastFactoid.get("TEXT"));

		lastFactoid = null;

	}

	/**
	 * Check for matches for speaking.
	 * 
	 * @param event
	 *            the event
	 */
	private void checkForMatchesForSpeaking(MessageEvent<Bot> event) {
		for (String s : event.getMessage().split(" ")) {
			if (s.length() < 2)
				continue;
			String query = "select * from " + this.getFormattedTableName()
					+ " where lower(precondition) like "
					+ Database.getEnclosedString("%" + s.toLowerCase());
			HashMap<String, Object> rand = null;
			try {
				if (Database.hasRow(query)) {
					rand = Database.getRandomRow(Database.select(query));
				}
			} catch (SQLException e) {
				// this only happens when reloading after dropping the table
			}
			if (rand == null)
				continue;

			String message;
			switch (rand.get("ACTION_TYPE").toString().trim()) {
			case "is":
			case "are":
				message = rand.get("PRECONDITION") + " "
						+ rand.get("ACTION_TYPE").toString().trim() + " "
						+ rand.get("TEXT");
				break;
			case "<reply>":
				message = rand.get("TEXT").toString();
				passMessage(event.getBot(), event.getChannel(),
						event.getUser(), message);
				lastFactoid = rand;
				return;
			case "<action>":
				message = rand.get("TEXT").toString();
				passEmote(event.getBot(), event.getChannel(), event.getUser(),
						message);
				lastFactoid = rand;
				return;
			default:
				message = "";
			}

			passMessage(event.getBot(), event.getChannel(), event.getUser(),
					event.getUser().getNick() + ", " + message);

			lastFactoid = rand;
			return;
		}
	}

	/**
	 * Check for matches for database.
	 * 
	 * @param event
	 *            the event
	 * @return true, if successful
	 * @throws SQLException
	 *             the sQL exception
	 */
	private boolean checkForMatchesForDatabase(MessageEvent<Bot> event)
			throws SQLException {
		boolean match = false;
		String message = event.getMessage();
		if (Util.hasLink(message))
			return false;

		String[] messageArr = message.split(" ");

		// TODO make this only match a-zA-Z inbetween <>
		if (message.matches(".+\\<(.*?)\\>.+")) {
			String[] args = new String[3];

			args[0] = message.substring(0, message.indexOf("<")).trim();
			if (!allowDuplicates
					&& Database.hasRow("select * from "
							+ this.getFormattedTableName()
							+ " where precondition='" + args[0] + "'"))
				return false;
			args[1] = message.substring(message.indexOf("<"),
					message.indexOf(">") + 1).trim();
			args[2] = message.substring(message.indexOf(">") + 1).trim();

			Database.insert(
					this.getFormattedTableName(),
					"server, channel, user_nick, precondition, action_type, text",
					Database.getEnclosedString(event.getBot().getServer())
							+ ","
							+ Database.getEnclosedString(event.getChannel()
									.getName())
							+ ","
							+ Database.getEnclosedString(event.getUser()
									.getNick()) + ","
							+ Database.getEnclosedString(args[0]) + ","
							+ Database.getEnclosedString(args[1]) + ","
							+ Database.getEnclosedString(args[2]));
			match = true;

		} else if (Util.hasArgs(message, 2, " is ")
				&& messageArr[1].equals("is")
				&& !messageArr[0].matches(".*\\W+.*")) {
			String[] args = Util.getArgs(message, 2, " is ");
			if (!allowDuplicates
					&& Database.hasRow("select * from "
							+ this.getFormattedTableName()
							+ " where precondition="
							+ Database.getEnclosedString(args[0])))
				return false;
			Database.insert(
					this.getFormattedTableName(),
					"server, channel, user_nick, precondition, action_type, text",
					Database.getEnclosedString(event.getBot().getServer())
							+ ","
							+ Database.getEnclosedString(event.getChannel()
									.getName())
							+ ","
							+ Database.getEnclosedString(event.getUser()
									.getNick()) + ","
							+ Database.getEnclosedString(args[0]) + ","
							+ Database.getEnclosedString("is") + ","
							+ Database.getEnclosedString(args[1]));
			match = true;

		} else if (Util.hasArgs(message, 2, " are ")
				&& messageArr[1].equals("are")
				&& !messageArr[0].matches(".*\\W+.*")) {
			String[] args = Util.getArgs(message, 2, " are ");
			if (!allowDuplicates
					&& Database.hasRow("select * from "
							+ this.getFormattedTableName()
							+ " where precondition="
							+ Database.getEnclosedString(args[0])))
				return false;
			Database.insert(
					this.getFormattedTableName(),
					"server, channel, user_nick, precondition, action_type, text",
					Database.getEnclosedString(event.getBot().getServer())
							+ ","
							+ Database.getEnclosedString(event.getChannel()
									.getName())
							+ ","
							+ Database.getEnclosedString(event.getUser()
									.getNick()) + ","
							+ Database.getEnclosedString(args[0]) + ","
							+ Database.getEnclosedString("are") + ","
							+ Database.getEnclosedString(args[1]));
			match = true;

		}
		if (match) {
			passMessage(event.getBot(), event.getChannel(), event.getUser(),
					"Okay, " + event.getUser().getNick() + ".");
			return true;
		}
		return false;
	}

}
