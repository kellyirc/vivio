/*
 * @author Kyle Kemp
 */
package commands;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import modules.Module;

public abstract class Command extends Module {
	
	public static final String CMD_SEQUENCE_DEFAULT = ".";

	public static final String CMD_SEQUENCE_NORMAL = "!";

	public static final String CMD_SEQUENCE_DEVELOPMENT = "~";

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
		case ACCESS_DEVELOPMENT: return CMD_SEQUENCE_DEVELOPMENT;
		case ACCESS_NORMAL: return CMD_SEQUENCE_NORMAL;
		default: return CMD_SEQUENCE_DEFAULT;
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
	@Getter @Setter(AccessLevel.PROTECTED) protected boolean isUsableInPM = false;

	protected String format() {
		return getCmdSequence() + getAliases();
	}
	
	public String toHelpString() {
		return getCmdSequence() + aliases + " - " + getHelpText();
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

	protected void invalidFormat(Bot bot, Channel chan, User u, String format) {
		passMessage(bot, chan, u, "Invalid format: "+format);
	}
	
	protected void invalidFormat(Bot bot, Channel chan, User u) {
		invalidFormat(bot, chan, u, format());
	}
}
