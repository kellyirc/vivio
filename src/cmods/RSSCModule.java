/*
 * @author Kyle Kemp
 * @description This module allows the bot to pull RSS/Atom feeds from across the internet to syndicate content into an IRC channel.
 * @basecmd rss
 * @category util
 */
package cmods;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import commands.Command;

import backend.Bot;
import backend.Database;
import backend.TimerThread;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class RSSCModule.
 */
@SuppressWarnings("unused")
public class RSSCModule extends Command {

	/** The rss check time. */
	private final int RSS_CHECK_TIME = 600;

	/** The most recent. */
	private HashMap<String, LinkedList<String>> mostRecent = new HashMap<>();

	/** The input. */
	private static SyndFeedInput input = new SyndFeedInput();

	/** The cache. */
	private static HashMap<String, SyndFeed> cache = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (Util.hasArgs(message, 4) && !message.contains("toggle")) {
			String[] args = Util.getArgs(message, 4);
			if (args[1].equals("add")) {
				try {
					if (Database.hasRow("select * from "
							+ getFormattedTableName() + " where channel='"
							+ chan.getName() + "' and server='"
							+ bot.getServer() + "' and feedurl='" + args[2]
							+ "'")) {
						passMessage(bot, chan, user,
								"That feed is already being watched, you silly goat.");
						return;
					}
					if (!Util.hasLink(args[2])) {
						passMessage(bot, chan, user, "That isn't a link!");
						return;
					}
					Database.insert(
							getFormattedTableName(),
							"server,channel,feedurl,feedname,feedowner,enabled",
							new Object[] { bot.getServer(), chan.getName(),
									args[2], args[3], user.getNick(), 1 },
							new boolean[] { true, true, true, true, true, false });
				} catch (SQLException e) {
					passMessage(bot, chan, user,
							"I was unable to properly add that feed.");
					return;
				}
				passMessage(bot, chan, user, "Successfully added feed \""
						+ args[3] + "\"");
				return;
			}

		} else if (Util.hasArgs(message, 3)) {
			String[] args = Util.getArgs(message, 3);
			switch (args[1]) {
			case "toggle-off":
				try {
					Database.execRaw("update " + getFormattedTableName()
							+ " set enabled=0 where channel='" + chan.getName()
							+ "' and server='" + bot.getServer()
							+ "' and feedname='" + args[2] + "'");
				} catch (SQLException e) {
					passMessage(bot, chan, user, "Unsuccessfully turned off "
							+ args[2]);
					return;
				}
				passMessage(bot, chan, user, "Successfully turned off "
						+ args[2]);
				break;
			case "toggle-on":
				try {
					Database.execRaw("update " + getFormattedTableName()
							+ " set enabled=1 where channel='" + chan.getName()
							+ "' and server='" + bot.getServer()
							+ "' and feedname='" + args[2] + "'");
				} catch (SQLException e) {
					passMessage(bot, chan, user, "Unsuccessfully turned off "
							+ args[2]);
					return;
				}
				passMessage(bot, chan, user, "Successfully turned on "
						+ args[2]);
				break;
			}
			return;
		}

		invalidFormat(bot, chan, user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		this.setActive(false);
		addAlias("rss");
		setName("RSS");
		setHelpText("I manage RSS feeds!");
		setAccessLevel(LEVEL_OPERATOR);
		setTableName("feeds");

		try {
			Database.createTable(
					this.getFormattedTableName(),
					"server char(20), channel char(30), feedurl varchar(600), feedname char(50), feedowner char(20), enabled smallint");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Bot.scheduleTask(new RssThread(), RSS_CHECK_TIME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	protected String format() {
		return super.format()
				+ " [add | toggle-[on|off]] [feedUrl] [feedName | ]";
	}

	/**
	 * The Class RssThread.
	 */
	private class RssThread extends TimerThread {

		/**
		 * Instantiates a new rss thread.
		 */
		public RssThread() {
			super("RSS");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			cache.clear();
			List<HashMap<String, Object>> feedData = null;
			try {
				feedData = Database.select("select * from "
						+ getFormattedTableName());
			} catch (SQLException e) {
				return;
			}

			for (HashMap<String, Object> row : feedData) {
				if ((int) row.get("ENABLED") == 0)
					continue;
				setContext(Bot.getBotByServer((String) row.get("SERVER")));
				if (getContext() == null)
					continue;
				String channel = (String) row.get("CHANNEL");
				if (!getContext().isInChannel(channel))
					continue;
				String url = (String) row.get("FEEDURL");

				SyndFeed feed;
				try {
					if (cache.containsKey(url))
						feed = cache.get(url);
					else {
						feed = input.build(new XmlReader(new URL(url)));
						cache.put(feed.getLink(), feed);
					}
				} catch (IllegalArgumentException | FeedException | IOException e) {
					continue;
				}
				if (!mostRecent.containsKey(feed.getTitle())) {

					mostRecent.put(feed.getTitle(), new LinkedList<String>());

					for (Object o : feed.getEntries()) {
						SyndEntry entry = (SyndEntry) o;
						mostRecent.get(feed.getTitle()).add(entry.getLink());
					}
					continue;
				}

				for (Object o : feed.getEntries()) {
					SyndEntry entry = (SyndEntry) o;
					if (mostRecent.get(feed.getTitle()).contains(
							entry.getLink())) {
						continue;
					}
					String feedFriendlyTitle = ((String) row.get("FEEDNAME"))
							.trim();
					passMessage(getContext(), getContext().getChannel(channel),
							null, "Latest entry for " + Colors.BOLD
									+ feedFriendlyTitle + Colors.NORMAL + ": "
									+ entry.getTitle() + " " + entry.getLink());
					mostRecent.get(feed.getTitle()).add(entry.getLink());
				}
			}
		}
	}
}
