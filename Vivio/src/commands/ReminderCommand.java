/*
 * @author Raymond Hammarling
 * @description This module lets you set reminders for a task after a specified time to anyone
 * @basecmd remind
 * @category utility
 */
package commands;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import lombok.NonNull;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.NickChangeEvent;

import backend.Bot;
import backend.Database;
import backend.TimerThread;
import backend.Util;

public class ReminderCommand extends Command {
	private final byte LAST_PARSED_NOTHING = 0;
	private final byte LAST_PARSED_DIGIT = 1;
	private final byte LAST_PARSED_LETTER = 2;
	private final byte LAST_PARSED_WHITESPACE = 3;
	
	private Map<String, Integer> wordAmount;
	
	private static Map<Reminder, ScheduledFuture<?>> reminders;
	
	@Override
	protected void initialize() {
		setName("Reminder");
		setHelpText("Tell me to remind you (or something) about something in a while!");
		addAlias("remind");
		setTableName("remindtasks");
		
		if(reminders == null) reminders = new HashMap<>();
		loadTasks();
		
		if(wordAmount == null) wordAmount = new LinkedHashMap<String, Integer>();
		wordAmount.put("a few", 3);
		wordAmount.put("a lot of", -79);
		wordAmount.put("alot of", -74);
		wordAmount.put("an", 1);
		wordAmount.put("a", 1);
		wordAmount.put("many", -25);
		wordAmount.put("tons of", -1000);
	}
	
	public void loadTasks() {
		try {
			for(ScheduledFuture<?> future : reminders.values()) {
				future.cancel(false);
			}
			reminders.clear();
			
			Database.createTable(getFormattedTableName(),
					"server varchar(60), target char(32), task varchar(500), endtime timestamp, setter char(32)");
			
			List<HashMap<String, Object>> tasks =
					Database.select("select id, server, target, task, endtime, setter from " + getFormattedTableName());
			
			for(HashMap<String, Object> taskRow : tasks) {
				int id = (Integer) taskRow.get("ID");
				String server = (String) taskRow.get("SERVER");
				String target = (String) taskRow.get("TARGET");
				String task = (String) taskRow.get("TASK");
				Timestamp endTime = (Timestamp) taskRow.get("ENDTIME");
				String setter = (String) taskRow.get("SETTER");
				
				if(server == null || target == null || task == null || endTime == null || setter == null) {
					debug("Row " + id + ": INVALID REMINDER ROW FOUND");
					continue;
				}
				
				Reminder reminder = new Reminder(id, server, target.trim(), task, endTime, setter.trim());
				
				long msUntilEnd = endTime.getTime() - System.currentTimeMillis();
				if(msUntilEnd < 0) msUntilEnd = 0;
				
				reminders.put(reminder, Bot.scheduleOneShotTask(new ReminderThread(reminder), msUntilEnd, TimeUnit.MILLISECONDS));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected String format() {
		return super.format() + " {target} [to [task] | in [duration]] ...";
	}

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		String reminderTarget = user.getNick();
		String task = "do something apparently quite important";
		long moment = 0;
		
		if(Util.hasArgs(message, 2)) {
			String params = message.split(" ", 2)[1]; //params contains everything in message after the command itself
			
			for(int i = 0; i < params.length(); i++) {
				int nextSpace = params.indexOf(" ", i);
				String token = nextSpace != -1 ? params.substring(i, nextSpace) : params.substring(i);
				
				switch(token) {
				case "to":
					i = nextSpace+1;
					task = "";
					
					for(; i < params.length(); i++) {
						if(params.startsWith("in", i) && isStartOfDelayDuration(params.substring(i))) break;
						
						task += params.charAt(i);
					}
					
					task = task.trim();
					--i;
					
					break;
					
				case "in":
					i = nextSpace+1;
					
					int currentNumber = 0;
					String unit = "";
					
					byte lastParsed = LAST_PARSED_NOTHING;
					
					for(; i < params.length(); i++) {
						if(Character.isDigit(params.charAt(i))) {
							if(lastParsed == LAST_PARSED_LETTER || lastParsed == LAST_PARSED_WHITESPACE ||
									i >= params.length()-1) {
								
								moment += currentNumber * getUnitToMillisecMultiplier(unit);
								currentNumber = 0;
								unit = "";
							}
							lastParsed = LAST_PARSED_DIGIT;
							
							currentNumber = currentNumber*10 + (params.charAt(i) - '0');
						}
						
						else if(Character.isLetter(params.charAt(i))) {
							boolean wordFound = false;
							for(String word : wordAmount.keySet()) {
								if(params.startsWith(word + " ", i)) {
									wordFound = true;
									currentNumber = wordAmount.get(word);
									if(currentNumber < 0) currentNumber = (new Random()).nextInt(-currentNumber);
									i += word.length()-1;
									lastParsed = LAST_PARSED_DIGIT;
									break;
								}
							}
							if(wordFound) continue;
							
							if(lastParsed == LAST_PARSED_WHITESPACE) {
								--i;
								break;
							}
							
							lastParsed = LAST_PARSED_LETTER;
							unit += params.charAt(i);
						}
						
						else if(Character.isWhitespace(params.charAt(i))) {
							if(lastParsed == LAST_PARSED_LETTER) lastParsed = LAST_PARSED_WHITESPACE;
						}
					}
					
					moment += currentNumber * getUnitToMillisecMultiplier(unit);
					
					break;
					
				default:
					if(i == 0) {
						if(!token.equals("me")) reminderTarget = token;
					}
					else {
						invalidFormat(bot, chan, user);
						return;
					}
					i = nextSpace;
					break;
				}
			}
		}
		
		boolean exitTrailRemovingLoop = false;
		while(task.length() > 0 && !exitTrailRemovingLoop) {
			switch(task.charAt(task.length()-1)) {
			case '.': case '!': case '?':
				task = task.substring(0, task.length()-1);
				break;
				
			default:
				exitTrailRemovingLoop = true;
				break;
			}
		}
		
		if(task.length() == 0) {
			invalidFormat(bot, chan, user);
			return; //NOTHING TO DO HERE
		}
		
		passMessage(bot, chan, user, "Alright " + user.getNick() + ", I'll tell "
				+ (reminderTarget.equals(user.getNick()) ? "you" : reminderTarget) + " to " + task + " " + ((moment > 0) ? "in "
				+ outputTime(moment) : "right now") + ", and stuff.");
		
		Reminder reminder =
				new Reminder(-1, bot.getServer(), reminderTarget, task, new Timestamp(System.currentTimeMillis() + moment), user.getNick());
		
		try {
			Database.insert(getFormattedTableName(),
					"server, target, task, endtime, setter",
					reminder.toDatabaseString());
			
			reminder.setId( Database.getLastGeneratedId(getFormattedTableName()) );
			
			debug("LastID is " + reminder.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		reminders.put(reminder, Bot.scheduleOneShotTask(new ReminderThread(reminder), moment, TimeUnit.MILLISECONDS));
	}
	
	public long getUnitToMillisecMultiplier(String unit) {
		switch(unit) {
		case "ms": case "millisec": case "millisecs": case "millisecond": case "milliseconds":
			return 1L;
		case "s": case "sec": case "secs": case "second": case "seconds":
			return 1000L;
		case "m": case "min": case "mins": case "minute": case "minutes":
			return 60*1000L;
		case "h": case "hour": case "hours":
			return 60*60*1000L;
		case "halfhour": case "halfhours":
			return 30*60*1000L;
		case "d": case "day": case "days":
			return 24*60*60*1000L;
		case "w": case "week": case "weeks": case "sennight": case "sennights":
			return 7*24*60*60*1000L;
		case "fortnight": case "fortnights":
			return 14*24*60*60*1000L;
		case "y": case "year": case "years":
			return 365*24*60*60*1000L;
		case "moment": case "moments":
			return 90*1000L;
		case "chelek": case "cheleks":
			return 3333L;
		case "rega": case "regas":
			return 43L;
		case "jiffy": case "jiffys": case "jiffies":
			return 17L;
		case "decade": case "decades":
			return 10*365*24*60*60*1000L;
		case "microfortnight": case "microfortnights":
			return 1209L;
		case "dogyear": case "dogyears":
			return 52*24*60*60*1000L;
		case "instant": case "instants":
			return 0;
		default:
			return 0;
		}
	}
	
	public boolean isStartOfDelayDuration(String param) {
		if(param.startsWith("in")) {
			for(int i = "in".length(); i < param.length(); i++) {
				if(Character.isWhitespace(param.charAt(i))) continue;
				
				if(Character.isDigit(param.charAt(i))) return true;
				
				if(Character.isLetter(param.charAt(i))) return false;
			}
		}
		
		return false;
	}
	
	public void onNickChange(NickChangeEvent<Bot> event) {
		for(Reminder reminder : reminders.keySet()) {
			if(reminder.getTarget().equals(event.getOldNick())) {
				reminder.setTarget(event.getNewNick());
				
				try {
					Database.execRaw("update " + getFormattedTableName()
							+ " set target='" + reminder.getTarget() + "' where id=" + reminder.getId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(reminder.getSetter().equals(event.getOldNick())) {
				reminder.setSetter(event.getNewNick());
				
				try {
					Database.execRaw("update " + getFormattedTableName()
							+ " set setter='" + reminder.getSetter() + "' where id=" + reminder.getId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Data
	public class Reminder {
		@NonNull private int id;
		@NonNull private String server;
		@NonNull private String target;
		@NonNull private String task;
		@NonNull private Timestamp endTime;
		@NonNull private String setter;
		
		public String toDatabaseString() {
			//			"server,		   target,			 task,		    endtime, 					 setter"
			return "'" + server + "', '" + target + "', '" + task + "', '" + endTime.toString() + "', '" + setter + "'";
		}
	}
	
	private class ReminderThread extends TimerThread {
		private Reminder reminderData = null;
		
		public ReminderThread(Reminder data) {
			super("ReminderThread");
			
			reminderData = data;
		}
		
		@Override
		public void run() {
			setContext(Bot.getBotByServer(reminderData.getServer()));
			
			String messageTarget = reminderData.getTarget();
			
			for(Channel channel : getContext().getChannels()) {
				for(User user : channel.getUsers()) {
					if(user.getNick().equals(reminderData.getTarget())) messageTarget = channel.getName();
				}
			}
			
			String message = "'Ey, you, " + reminderData.getTarget() + "! "
					+ (reminderData.getTarget().equals(reminderData.getSetter()) ? "You" : reminderData.getSetter())
					+ " wanted me to remind you to " + reminderData.getTask() + "!";
			
			getContext().sendMessage(messageTarget, message);
			if(reminderData.getTarget() != messageTarget) getContext().sendMessage(reminderData.getTarget(), message);
			getContext().sendNotice(reminderData.getTarget(), message);
			
			if(reminderData.getId() != -1) {
				try {
					Database.execRaw("delete from " + getFormattedTableName() + " where id=" + reminderData.getId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			reminders.remove(reminderData);
		}
		
	}
	
	public String outputTime(long time) {
		long ms = time % 1000;
		time /= 1000;
		long seconds = time % 60;
		long minutes = (time % 3600) / 60;
		long hours = (time % 86400) / 3600;
		long days = time / 86400;
		
		String output = "";
		if(days != 0) output += (output.length()>0 ? ", " : "") + days + " day" + (days > 1 ? "s" : "");
		if(hours != 0) output += (output.length()>0 ? ", " : "") + hours + " hour" + (hours > 1 ? "s" : "");
		if(minutes != 0) output += (output.length()>0 ? ", " : "") + minutes + " minute" + (minutes > 1 ? "s" : "");
		if(seconds != 0) output += (output.length()>0 ? ", " : "") + seconds + " second" + (seconds > 1 ? "s" : "");
		if(ms != 0) output += (output.length()>0 ? ", " : "") + ms + " millisecond" + (ms > 1 ? "s" : "");
		
		return output;
	}
}
