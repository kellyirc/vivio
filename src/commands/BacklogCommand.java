/*
 * @author Rahat Ahmed
 * 
 * @description This module will send the user a snippet of the log recorded
 * in the channel it was used in. Parameters can include the max number of
 * lines, a measure of time, or a nickname that has recently quit.
 * 
 * @basecmd backlog
 * 
 * @category utility
 */package commands;

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
import cmods.LoggingCModule;
import cmods.LoggingCModule.EventType;

public class BacklogCommand extends Command
{
	public static final int PASTEBIN_LIMIT = 5;
	public static final int MAX_LINES = 1000;
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
				int n = 0;
				if(cmd.length == 2)
				{
					
					try
					{
						n = Integer.parseInt(cmd[1]);
					} catch (NumberFormatException e) { //its not a number, must be a nickname
						List<HashMap<String, Object>> query = Database.select("SELECT id FROM LoggingCModule_logs WHERE CHANNEL="+Database.getEnclosedString(chan.getName())+" AND user_name="+Database.getEnclosedString(cmd[1])+" AND (event_type="+LoggingCModule.EventType.QUIT.ordinal()+" OR event_type="+LoggingCModule.EventType.KICK.ordinal()+" OR event_type="+LoggingCModule.EventType.PART.ordinal()+") ORDER BY id DESC", 1);
						if(query.size()==0)
						{
							passMessage(bot, chan, user, "Unable to find last quit/part/kick of "+cmd[1]);
							return;
						}
						int id = (int) query.get(0).get("ID");
						query = Database.select("SELECT id FROM LoggingCModule_logs WHERE id > "+id+" AND CHANNEL="+Database.getEnclosedString(chan.getName()));
						n = query.size();
					}
					
				}
				else
				{
					//need even number of parameters (plus the cmd sequence so its odd)
					if(cmd.length%2 == 0)
					{
						passMessage(bot, chan, user, "Invalid time parameter");
						return;
					}
					long time = 0;
					for(int k=1;k<cmd.length;k+=2)
					{
						long amount = Long.parseLong(cmd[k]);
						long unit = getUnitToMillisecMultiplier(cmd[k+1]);
						if(unit == -1)
						{
							passMessage(bot, chan, user, "Invalid time parameter");
							return;
						}
						time += amount * unit;
						
					}
					time = System.currentTimeMillis() - time;
					String timestamp = Database.formatTimestamp(time);
					List<HashMap<String, Object>> query = Database.select("SELECT * FROM LoggingCModule_logs WHERE CHANNEL="+Database.getEnclosedString(chan.getName())+" AND time > "+timestamp+"", 1);
					int id = (Integer) query.get(0).get("ID");
					query = Database.select("SELECT id FROM LoggingCModule_logs WHERE id > "+id+" AND CHANNEL="+Database.getEnclosedString(chan.getName()));
					n = query.size();
				}
				
				if(n > MAX_LINES)
				{
					passMessage(bot, chan, user, "Requested number of lines "+n+" is too high. Returning maximum of "+MAX_LINES);
					n = MAX_LINES;
				}
				
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
			}  catch (SQLException e)
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
	
	/**
	 * Gets the unit to millisec multiplier.
	 * 
	 * @param unit
	 *            the unit
	 * @return the unit to millisec multiplier
	 */
	public static long getUnitToMillisecMultiplier(String unit) {
		switch (unit) {
		case "ms":
		case "millisec":
		case "millisecs":
		case "millisecond":
		case "milliseconds":
			return 1L;
		case "atom":
		case "atoms":
			return 160L; // About 15/94 of a second
		case "s":
		case "sec":
		case "secs":
		case "second":
		case "seconds":
			return 1000L;
		case "m":
		case "min":
		case "mins":
		case "minute":
		case "minutes":
			return 60 * 1000L;
		case "h":
		case "hour":
		case "hours":
			return 60 * 60 * 1000L;
		case "pahar":
		case "pahars":
		case "paher":
		case "pahers":
			return 3 * 60 * 60 * 1000L;
		case "moment":
		case "moments":
			return 90 * 1000L;
		case "chelek":
		case "cheleks":
			return 3333L;
		case "rega":
		case "regas":
			return 43L;
		case "jiffy":
		case "jiffys":
		case "jiffies":
			return 17L;

		default:
			if (unit.startsWith("dog-"))
				return getUnitToMillisecMultiplier(unit.substring(4)) / 7L;
			if (unit.startsWith("dog"))
				return getUnitToMillisecMultiplier(unit.substring(3)) / 7L;

			if (unit.startsWith("half-"))
				return getUnitToMillisecMultiplier(unit.substring(5)) / 2L;
			if (unit.startsWith("half"))
				return getUnitToMillisecMultiplier(unit.substring(4)) / 2L;

			if (unit.startsWith("quarter-"))
				return getUnitToMillisecMultiplier(unit.substring(8)) / 4L;
			if (unit.startsWith("quarter"))
				return getUnitToMillisecMultiplier(unit.substring(7)) / 4L;

			return -1;
		}
	}
}
