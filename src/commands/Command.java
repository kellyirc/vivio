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

// TODO: Auto-generated Javadoc
/**
 * The Class Command.
 */
public abstract class Command extends Module {

	/** The Constant CMD_SEQUENCE_DEFAULT. */
	public static final String CMD_SEQUENCE_DEFAULT = ".";

	/** The Constant CMD_SEQUENCE_NORMAL. */
	public static final String CMD_SEQUENCE_NORMAL = "!";

	/** The Constant CMD_SEQUENCE_DEVELOPMENT. */
	public static final String CMD_SEQUENCE_DEVELOPMENT = "~";

	/** The aliases. */
	private List<String> aliases;

	/**
	 * Instantiates a new command.
	 */
	public Command() {
		super();
	}

	/**
	 * Gets the aliases.
	 * 
	 * @return the aliases
	 */
	public List<String> getAliases() {
		if (aliases == null)
			aliases = new ArrayList<String>();
		return aliases;
	}

	/**
	 * Gets the cmd sequence.
	 * 
	 * @return the cmd sequence
	 */
	public String getCmdSequence() {
		switch (getAccessMode()) {
		case ACCESS_DEVELOPMENT:
			return CMD_SEQUENCE_DEVELOPMENT;
		case ACCESS_NORMAL:
			return CMD_SEQUENCE_NORMAL;
		default:
			return CMD_SEQUENCE_DEFAULT;
		}
	}

	/**
	 * Execute.
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
	public abstract void execute(Bot bot, Channel chan, User user,
			String message);

	// commands with different aliases that function differently can act in
	// different ways depending on the alias
	// user and chan may be null
	/**
	 * Execute.
	 * 
	 * @param alias
	 *            the alias
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param user
	 *            the user
	 * @param message
	 *            the message
	 */
	public void execute(String alias, Bot bot, Channel chan, User user,
			String message) {
		execute(bot, chan, user, message);
	}

	// whether or not this command can stop the execution of other commands
	// after it executes.
	/** The stops execution. */

	/**
	 * Checks if is stops execution.
	 * 
	 * @return true, if is stops execution
	 */
	@Getter
	/**
	 * Sets the stops execution.
	 *
	 * @param stopsExecution the new stops execution
	 */
	@Setter(AccessLevel.PROTECTED)
	protected boolean stopsExecution = false;

	/**
	 * Checks if is usable in pm.
	 * 
	 * @return true, if is usable in pm
	 */
	@Getter
	/**
	 * Sets the usable in pm.
	 *
	 * @param isUsableInPM the new usable in pm
	 */
	@Setter(AccessLevel.PROTECTED)
	protected boolean isUsableInPM = false;

	/**
	 * Format.
	 * 
	 * @return the string
	 */
	protected String format() {
		return getCmdSequence() + getAliases();
	}

	/**
	 * To help string.
	 * 
	 * @return the string
	 */
	public String toHelpString() {
		return getCmdSequence() + aliases + " - " + getHelpText();
	}

	/**
	 * Adds the alias.
	 * 
	 * @param alias
	 *            the alias
	 */
	protected void addAlias(String alias) {
		getAliases().add(alias);
	}

	/**
	 * Checks for alias.
	 * 
	 * @param commandString
	 *            the command string
	 * @return true, if successful
	 */
	public boolean hasAlias(String commandString) {
		for (String s : getAliases()) {
			if (commandString.toLowerCase().equals(s.toLowerCase()))
				return true;
		}
		return false;
	}

	/**
	 * Invalid format.
	 * 
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param u
	 *            the u
	 * @param format
	 *            the format
	 */
	protected void invalidFormat(Bot bot, Channel chan, User u, String format) {
		passMessage(bot, chan, u, "Invalid format: " + format);
	}

	/**
	 * Invalid format.
	 * 
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param u
	 *            the u
	 */
	protected void invalidFormat(Bot bot, Channel chan, User u) {
		invalidFormat(bot, chan, u, format());
	}
}
