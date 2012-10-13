/*
 * @author Kyle Kemp
 * @description This module allows the bot to log all chat and generate statistics for every channel it is a part of.
 * @basecmd generate-stats
 * @category misc
 */
package cmods;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.HalfOpEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.SuperOpEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.VoiceEvent;

import commands.Command;

import backend.Bot;
import backend.Database;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class LoggingCModule.
 */
public class LoggingCModule extends Command {

	
	public enum EventType { ACTION, CONNECT, DISCONNECT, JOIN, KICK, MESSAGE, MODE, NICK_CHANGE, NOTICE, PART, PRIVATE_MESSAGE, QUIT, TOPIC };
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(final Bot bot, final Channel chan, final User user,
			String message) {
		if (chan == null)
			return;

		if (Util.hasArgs(message, 2)) {
			String[] args = Util.getArgs(message, 2);
			switch (args[1]) {
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

		passMessage(bot, chan, user, "I will begin generating statistics for "
				+ chan.getName() + " now.");

		// TODO total domination statistic, when you are the name for every
		// single statistic
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<HashMap<String, Object>> returned = null;
				try {
					returned = getDataForChannel(chan);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				if (returned == null) {
					passMessage(
							bot,
							chan,
							user,
							"I could not generate statistics for "
									+ chan.getName());
					return;
				}
				passMessage(bot, chan, user, "I have " + returned.size()
						+ " recorded messages from " + chan.getName() + ".");

				displayRandomQuote(bot, chan, user, returned);
				try {
					displayRandomLink(bot, chan, user, returned);
				} catch (IOException e) {
					e.printStackTrace();
				}

				HashMap<String, Integer> httpcount = new HashMap<>();
				HashMap<String, Integer> cmdcount = new HashMap<>();

				// these are doubles so they don't have to be cast later
				double httpNum = 0;
				double cmdNum = 0;

				for (HashMap<String, Object> column : returned) {
					String message = column.get("MESSAGE").toString();
					String sender = column.get("USER_NAME").toString().trim();
					if (Util.hasLink(message)) {
						httpNum++;
						if (httpcount.containsKey(sender))
							httpcount.put(sender, httpcount.get(sender) + 1);
						else
							httpcount.put(sender, 1);
					} else if (message.startsWith("!")) {
						cmdNum++;
						if (cmdcount.containsKey(sender))
							cmdcount.put(sender, cmdcount.get(sender) + 1);
						else
							cmdcount.put(sender, 1);
					}
				}

				Map.Entry<String, Integer> maxHttp = getMax(httpcount);
				Map.Entry<String, Integer> maxCmd = getMax(cmdcount);

				DecimalFormat twoDForm = new DecimalFormat("#.##");
				// TODO most popular domain
				passMessage(
						bot,
						chan,
						user,
						"Total links posted: "
								+ (int) httpNum
								+ " ("
								+ twoDForm.format((httpNum / returned.size()) * 100)
								+ "% of all messages).");
				if (maxHttp != null)
					passMessage(
							bot,
							chan,
							user,
							"Most links posted: "
									+ maxHttp.getKey()
									+ ", with "
									+ maxHttp.getValue()
									+ " links ("
									+ twoDForm.format((maxHttp.getValue() / httpNum) * 100)
									+ "% of all links).");

				passMessage(
						bot,
						chan,
						user,
						"Total commands used: "
								+ (int) cmdNum
								+ " ("
								+ twoDForm.format((cmdNum / returned.size()) * 100)
								+ "% of all messages).");
				if (maxCmd != null)
					passMessage(
							bot,
							chan,
							user,
							"Most commands used: "
									+ maxCmd.getKey()
									+ ", with "
									+ maxCmd.getValue()
									+ " commands ("
									+ twoDForm.format((maxCmd.getValue() / cmdNum) * 100)
									+ "% of all commands).");

			}
		}).start();
	}

	/**
	 * Gets the max.
	 * 
	 * @param httpcount
	 *            the httpcount
	 * @return the max
	 */
	private Map.Entry<String, Integer> getMax(HashMap<String, Integer> httpcount) {
		Map.Entry<String, Integer> curMax = null;

		for (Map.Entry<String, Integer> entry : httpcount.entrySet()) {
			if (curMax == null
					|| httpcount.get(entry.getKey()) > httpcount.get(curMax
							.getKey()))
				curMax = entry;
		}
		return curMax;
	}

	/**
	 * Display random quote.
	 * 
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param user
	 *            the user
	 * @param returned
	 *            the returned
	 */
	private void displayRandomQuote(final Bot bot, final Channel chan,
			final User user, List<HashMap<String, Object>> returned) {
		HashMap<String, Object> rand;
		String message;
		do {
			rand = Database.getRandomRow(returned);
			message = rand.get("MESSAGE").toString();
		} while (message.startsWith("!") || message.split(" ").length < 3
				|| message.length() < 10);
		passMessage(bot, chan, user,
				"Random quote: <" + ((String) rand.get("USER_NAME")).trim()
						+ "> " + message);
	}

	/**
	 * Display random link.
	 * 
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param user
	 *            the user
	 * @param returned
	 *            the returned
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void displayRandomLink(final Bot bot, final Channel chan,
			final User user, List<HashMap<String, Object>> returned)
			throws MalformedURLException, IOException {
		HashMap<String, Object> rand;
		String link;
		do {
			rand = Database.getRandomRow(returned);
			link = rand.get("MESSAGE").toString();
		} while (!Util.hasLink(link) || Util.parseLink(link).equals(""));
		passMessage(
				bot,
				chan,
				user,
				"Random link: <" + ((String) rand.get("USER_NAME")).trim()
						+ "> " + Util.extractLink(link) + " -- "
						+ Util.parseLink(link));
	}

	/**
	 * Gets the data for channel.
	 * 
	 * @param chan
	 *            the chan
	 * @return the data for channel
	 * @throws SQLException
	 *             the sQL exception
	 */
	private List<HashMap<String, Object>> getDataForChannel(final Channel chan)
			throws SQLException {
		List<HashMap<String, Object>> returned = Database
				.select("select * from " + getFormattedTableName()
						+ " where channel="
						+ Database.getEnclosedString(chan.getName()));
		return returned;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
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
					"server char(20), channel char(30), user_name char(25), event_type smallint, message varchar(600), time timestamp not null default current timestamp");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void log(String server, Channel chan, User user, EventType type, String message) throws SQLException
	{
		if(chan == null)
			Database.insert(
					getFormattedTableName(),
					"server, user_name, event_type, message",
					Database.getEnclosedString(server) + ","
							+ Database.getEnclosedString(user.getNick()) + ","
							+ type.ordinal() + ","
							+ Database.getEnclosedString(message));
		else
		Database.insert(
				getFormattedTableName(),
				"server, channel, user_name, event_type, message",
				Database.getEnclosedString(server) + ","
						+ Database.getEnclosedString(chan.getName()) + ","
						+ Database.getEnclosedString(user.getNick()) + ","
						+ type.ordinal() + ","
						+ Database.getEnclosedString(message));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events
	 * .MessageEvent)
	 */
	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception 
	{
		super.onMessage(event);
		log(event.getBot().getServer(), event.getChannel(), event.getUser(),
				EventType.MESSAGE, event.getMessage());
	}

	@Override
	public void onAction(ActionEvent<Bot> event) throws Exception
	{
		super.onAction(event);
		log(event.getBot().getServer(), event.getChannel(), event.getUser(),
				EventType.ACTION, event.getMessage());
	}

	@Override
	public void onConnect(ConnectEvent<Bot> event) throws Exception
	{
		super.onConnect(event);
		log(event.getBot().getServer(), null, event.getBot().getUserBot(),
				EventType.CONNECT, "Connected to "+ event.getBot().getServer() + ".");
	}
	
	@Override
	public void onDisconnect(DisconnectEvent<Bot> event) throws Exception
	{
		super.onDisconnect(event);
		log(event.getBot().getServer(), null, event.getBot().getUserBot(),
				EventType.DISCONNECT, "Disconnected from "+ event.getBot().getServer() + ".");
	}
	
//	@Override
//	public void onHalfOp(HalfOpEvent<Bot> event) throws Exception
//	{
//		super.onHalfOp(event);
//		log(event.getBot().getServer(), event.getChannel(), event.getRecipient(), EventType.HALF_OP, event.getRecipient().getNick() + 
//				" has "+((event.isHalfOp())?"recieved":"lost")+" half op status.");
//	}
	
	@Override
	public void onJoin(JoinEvent<Bot> event) throws Exception
	{
		super.onJoin(event);
		log(event.getBot().getServer(), event.getChannel(), event.getUser(),
				EventType.JOIN, event.getUser().getNick() + " has joined "+ event.getChannel().getName() + ".");
	}

	@Override
	public void onKick(KickEvent<Bot> event) throws Exception
	{
		super.onKick(event);
		log(event.getBot().getServer(), event.getChannel(), event.getRecipient(),
				EventType.KICK, event.getRecipient().getNick() + " has been kicked from "+ event.getChannel().getName() + 
				". (Reason: " + event.getReason() + ")");
	}
	
	@Override
	public void onMode(ModeEvent<Bot> event) throws Exception
	{
		super.onMode(event);
		log(event.getBot().getServer(), event.getChannel(), event.getUser(),
				EventType.MODE, event.getUser().getNick() + " set mode " + event.getMode() + " " + event.getChannel().getName());
	}

	@Override
	public void onNickChange(NickChangeEvent<Bot> event) throws Exception
	{
		super.onNickChange(event);
		for(Channel chan:event.getUser().getChannels())
			log(event.getBot().getServer(), chan, event.getUser(),
				EventType.NICK_CHANGE, event.getOldNick() + " has changed his nick to "+ event.getNewNick() + ".");
	}

	@Override
	public void onNotice(NoticeEvent<Bot> event) throws Exception
	{
		super.onNotice(event);
		log(event.getBot().getServer(), event.getChannel(), event.getUser(),
				EventType.NOTICE, "Notice from "+event.getUser().getNick()+": "+event.getMessage());
	}

//	@Override
//	public void onOp(OpEvent<Bot> event) throws Exception
//	{
//		super.onOp(event);
//		log(event.getBot().getServer(), event.getChannel(), event.getRecipient(), EventType.OP, event.getRecipient().getNick() + 
//				" has "+((event.isOp())?"recieved":"lost")+" op status.");
//	}
	
	@Override
	public void onPart(PartEvent<Bot> event) throws Exception
	{
		super.onPart(event);
		log(event.getBot().getServer(), event.getChannel(), event.getUser(),
				EventType.PART, event.getUser().getNick() + " has left "+ event.getChannel().getName() + " (Reason: "+event.getReason()+").");
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent<Bot> event)
			throws Exception
	{
		super.onPrivateMessage(event);
		log(event.getBot().getServer(), null, event.getUser(),
				EventType.MESSAGE, event.getMessage());
	}
	
	@Override
	public void onQuit(QuitEvent<Bot> event) throws Exception
	{
		super.onQuit(event);
		for(Channel chan:event.getUser().getChannels())
		log(event.getBot().getServer(), chan, event.getUser(),
				EventType.QUIT, event.getUser().getNick() + " has quit "+ chan.getName() + " (Reason: "+event.getReason()+").");
	}
	
//	@Override
//	public void onSuperOp(SuperOpEvent<Bot> event) throws Exception
//	{
//		super.onSuperOp(event);
//		log(event.getBot().getServer(), event.getChannel(), event.getRecipient(), EventType.SUPER_OP, event.getRecipient().getNick() + 
//				" has "+((event.isSuperOp())?"recieved":"lost")+" super op status.");
//	}
	
	
	@Override
	public void onTopic(TopicEvent<Bot> event) throws Exception
	{
		super.onTopic(event);
		if(event.isChanged())
			log(event.getBot().getServer(), event.getChannel(), event.getUser(), EventType.TOPIC, event.getUser().getNick()+" set the topic: "+ event.getTopic());
	}
	
//	@Override
//	public void onVoice(VoiceEvent<Bot> event) throws Exception
//	{
//		super.onVoice(event);
//		log(event.getBot().getServer(), event.getChannel(), event.getRecipient(), EventType.VOICE, event.getRecipient().getNick() + 
//				" has "+((event.isVoice())?"recieved":"lost")+" voice status.");
//	}

	
	
}
