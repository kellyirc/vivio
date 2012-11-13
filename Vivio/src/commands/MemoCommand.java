/*
 * @author Rahat Ahmed
 * 
 * @description This module allows a user to leave a message for other users not
 * currently present. They will receive their memos the next time the bot sees
 * them.
 * 
 * @basecmd memo
 * 
 * @category utility
 */
package commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.UserListEvent;

import backend.Bot;
import backend.Database;
import backend.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class MemoCommand.
 */
public class MemoCommand extends Command
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize()
	{
		this.setHelpText("Save a message for someone who is not in the channel. They will receive the message once they enter the channel. Separate multiple recipients with commas and separate aliases for a single user with '/'s.");
		this.setName("Memo");
		addAlias("memo");
		setTableName("memos");
		try
		{
			Database.createTable(getFormattedTableName(),
					"sender VARCHAR(30), recipient VARCHAR(30), time TIMESTAMP, msg VARCHAR(600)");
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#format()
	 */
	@Override
	protected String format()
	{
		return super.format() + " [nick] [message]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
		if (getTarget(chan, user).equals(user.getNick()))
		{
			passMessage(bot, chan, user,
					"Sorry, I only work in channels! Use memoserv if you want private memos.");
			return;
		}
		if (Util.hasArgs(message, 3))
		{
			String[] args = message.split(" ", 3);
			String[] nicks = args[1].split(",");
			String msg = args[2];
			for (String nick : nicks)
			{
				for (String alias : nick.split("/"))
				{
					System.out.println(alias);
					if (chan.getUsers().contains(bot.getUser(alias)))
					{
						passMessage(bot, chan, user,
								"Nice try, " + alias
										+ " is already in this channel. Man up and talk to him/her yourself!");
						return;
					}
				}
				try
				{
					Database.insert(
							getFormattedTableName(),
							"sender, recipient, time, msg",
							Database.getEnclosedString(user.getNick())
									+ ","
									+ Database.getEnclosedString(nick)
									+ ","
									+ Database.formatTimestamp(System
											.currentTimeMillis())
									+ ","
									+ Database.getEnclosedString(msg
											.replaceAll("'", "''")));
					passMessage(bot, chan, user, "Okay, i'll let " + nick
							+ " know.");

				} catch (SQLException e)
				{
					e.printStackTrace();
					passMessage(bot, chan, user, "Freek sucks ass at SQL.");
				}
				}

		} else
		{
			invalidFormat(bot, chan, user);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onJoin(org.pircbotx.hooks.events.JoinEvent
	 * )
	 */
	@Override
	public void onJoin(JoinEvent<Bot> e) throws Exception
	{
		super.onJoin(e);

		if (e.getChannel().getUsers().size() == 1)
			e.getBot().waitFor(UserListEvent.class);
		String nick = e.getUser().getNick();
		// if bot is joining channel, check for memos for people in that channel
		if (nick.equals(e.getBot().getNick()))
		{
			checkChanMemos(e.getChannel(), e.getBot());
		} else
		// check if entering user has a memo
		{
			checkNickMemos(e.getUser(), e.getBot());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onNickChange(org.pircbotx.hooks.events
	 * .NickChangeEvent)
	 */
	@Override
	public void onNickChange(NickChangeEvent<Bot> e) throws Exception
	{
		super.onNickChange(e);
		// check if new nick has a memo
		checkNickMemos(e.getUser(), e.getBot());
	}

	/**
	 * Check nick memos.
	 * 
	 * @param user
	 *            the user
	 * @param b
	 *            the b
	 */
	private void checkNickMemos(User user, Bot b)
	{
		try
		{
			String nick = user.getNick();
			String escapedNick = Database.getEnclosedString(nick).toLowerCase();
			// TODO: split nick by '/'s for aliases
			// nick = nick.replace
			List<HashMap<String, Object>> returned = Database
					.select("SELECT * FROM " + getFormattedTableName()
							+ " WHERE LOCATE(" + escapedNick
							+ ",lower(RECIPIENT)) > 0");
			System.out.println(returned);
			if (returned.isEmpty())
				return;
			// ArrayList<Integer> IDsToDelete = new ArrayList<Integer>();
			for (HashMap<String, Object> memo : returned)
			{
				String sender = memo.get("SENDER").toString();
				// String chan = memo.get("CHAN").toString();
				String timestamp = memo.get("TIME").toString();
				String msg = memo.get("MSG").toString();
				String recipients = memo.get("RECIPIENT").toString();
				for (String recipient : recipients.split("/"))
				{
					System.out.println(recipient);
					if (recipient.toLowerCase().equals(nick.toLowerCase()))
					{
						b.sendMessage(user, "A memo for " + nick + "! "
								+ sender + " said, \"" + msg + "\" at "
								+ timestamp);
						// IDsToDelete.add(Integer.parseInt(memo.get("ID").toString()));
						Database.execRaw("DELETE FROM "
								+ getFormattedTableName() + " WHERE ID="
								+ memo.get("ID"));
						break;
					}
				}
				// passMessage(e.getBot(),e.getBot().getChannel(chan),null,"A memo for "+nick+"! "+sender+" said, \""+msg+"\" at "+timestamp);

			}

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Check chan memos.
	 * 
	 * @param chan
	 *            the chan
	 * @param b
	 *            the b
	 */
	private void checkChanMemos(Channel chan, Bot b)
	{
		// TODO: Split the nicks by '/' for aliases
		for (User user : chan.getUsers())
		{
			checkNickMemos(user, b);
		}
	}
}
