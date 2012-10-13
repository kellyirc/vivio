package commands;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Database;
import backend.Util;
import cmods.LoggingCModule.EventType;

public class BacklogCommand extends Command
{
	public static final int PASTEBIN_LIMIT = 5;
	private static final SimpleDateFormat timestampFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	@Override
	protected void initialize()
	{
		setHelpText("Get the last few lines spoken in that channel. You can specify n number of lines, or a nickname to get all the lines after the last time that nick left the channel.");
		addAlias("backlog");
		setName("Backlog");
	}

	@Override
	protected String format()
	{
		return super.format() + "[num-lines]";
	}
	
	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
		String cmd[] = message.split(" ");
		if(cmd.length >= 2)
		{
			try
			{
				int n = Integer.parseInt(cmd[1]);
				if(chan == null)
				{
					passMessage(bot, chan, user, "You have to use this command in a channel!");
				}
				else if(n <= PASTEBIN_LIMIT)
				{
					String[] log = getBacklog(n, chan.getName());
					for(String s:log)
						if(s!=null)
							bot.sendMessage(user, s);
				}
				else
				{
					String[] log = getBacklog(n, chan.getName());
					StringBuilder text = new StringBuilder();
					for(String s:log)
						if(s!=null)
							text.append(s);
					passMessage(bot, chan, user, Util.pastebin(text.toString()));
				}
			} catch (NumberFormatException e) {
				passMessage(bot, chan, user, "That's not an integer!");
			} catch (SQLException e)
			{
				passMessage(bot, chan, user, "bluh bluh Freek sucks at SQL!");
				e.printStackTrace();
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
			
		}
	}
	
	public String[] getBacklog(int n, String chan) throws SQLException, ParseException
	{
		List<HashMap<String, Object>> query = Database.select("SELECT * FROM LoggingCModule_logs WHERE CHANNEL="+Database.getEnclosedString(chan)+" ORDER BY id DESC", n+1);
		StringBuffer backlog = new StringBuffer();
		String[] log = new String[n];
		int pos = 0;
		for(int k=query.size()-1;k>=1;k--)
		{
			HashMap<String, Object> row = query.get(k);
			backlog.append("[");
			backlog.append(timestampFormat.format(new Date(((Timestamp) row.get("TIME")).getTime())));
			backlog.append("] ");
			if(((Integer)row.get("EVENT_TYPE")) == EventType.MESSAGE.ordinal())
			{
				backlog.append("<");
				backlog.append(((String) row.get("USER_NAME")).trim());
				backlog.append("> ");
			}
			backlog.append(((String) row.get("MESSAGE")).trim());
			backlog.append("\n");
			log[pos++] = backlog.toString();
			backlog = new StringBuffer();
		}
		return log;
	}
}
