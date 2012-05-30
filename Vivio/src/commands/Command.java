package commands;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import modules.Module;

public abstract class Command extends Module {

	@Getter @Setter(AccessLevel.PROTECTED) private TimerTask task;
	@Getter @Setter(AccessLevel.PROTECTED) private int secondsDelay;
	
	private List<String> aliases;
	
	public Command() {
		super();
	}
	
	public List<String> getAliases() {
		if(aliases == null) aliases = new ArrayList<String>();
		return aliases;
	}

	public String getCmdSequence() {
		switch(getAccessMode()) {
		case ACCESS_DEVELOPMENT: return "~";
		case ACCESS_NORMAL: return "!";
		default: return ".";
		}
	}
	
	public abstract void execute(Bot bot, Channel chan, User user, String message);
	
	//commands with different aliases that function differently can act in different ways depending on the alias
	//user and chan may be null
	public void execute(String alias, Bot bot, Channel chan, User user, String message) {
		execute(bot, chan, user, message);
	}

	//whether or not this command can stop the execution of other commands after it executes.
	@Getter @Setter(AccessLevel.PROTECTED)	protected boolean stopsExecution = false;

	protected String format() {
		return getCmdSequence() + getAliases();
	}
	
	public String toHelpString() {
		return getCmdSequence() + aliases + " - " + getHelpText();
	}
	
	public String getTarget(Channel c, User u) {
		assert(c != null && u != null);
		return c == null ? (
							u == null ? null : u.getNick()
						)
							: c.getName();
	}
	
	public void passMessage(Bot b, Channel c, User u, String s) {
		String target = getTarget(c, u);
		if(target == null) {
			System.out.println(s);
		} else {
			b.sendMessage(getTarget(c, u), s);
		}
	}

	protected void addAlias(String alias) {
		getAliases().add(alias);
	}
	
	public boolean hasAlias(String commandString) {
		for(String s : getAliases()) {
			if(commandString.toLowerCase().startsWith(s.toLowerCase())) return true;
		}
		return false;
	}

	protected void invalidFormat(Bot bot, Channel chan, User u) {
		passMessage(bot, chan, u, "Invalid format: "+format());
	}
}
