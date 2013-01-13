/*
 * @author Kyle Kemp
 */
package modules;

import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;

import backend.Bot;
import backend.Constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

// TODO: Auto-generated Javadoc
/**
 * The Class Module.
 */
public abstract class Module extends ListenerAdapter<Bot> implements Constants {

	/**
	 * Instantiates a new module.
	 */
	public Module() {
		initialize();
	}

	/** The random. */
	protected static Random random = new Random();

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Getter
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	@Setter(AccessLevel.PROTECTED)
	private String name;

	/**
	 * Gets the table name.
	 * 
	 * @return the table name
	 */
	@Getter
	/**
	 * Sets the table name.
	 *
	 * @param tableName the new table name
	 */
	@Setter(AccessLevel.PROTECTED)
	private String tableName;

	/**
	 * Checks if is active.
	 * 
	 * @return true, if is active
	 */
	@Getter
	/**
	 * Sets the active.
	 *
	 * @param isActive the new active
	 */
	@Setter
	private boolean isActive = true;

	/**
	 * Gets the help text.
	 * 
	 * @return the help text
	 */
	@Getter
	/**
	 * Sets the help text.
	 *
	 * @param helpText the new help text
	 */
	@Setter(AccessLevel.PROTECTED)
	private String helpText = "This is the default help text!";

	// whether or not the command is available in the current bots mode.
	/** The access mode. */

	/**
	 * Gets the access mode.
	 * 
	 * @return the access mode
	 */
	@Getter
	/**
	 * Sets the access mode.
	 *
	 * @param accessMode the new access mode
	 */
	@Setter(AccessLevel.PROTECTED)
	private short accessMode = ACCESS_NORMAL;

	// the level of access for the module
	/** The access level. */

	/**
	 * Gets the access level.
	 * 
	 * @return the access level
	 */
	@Getter
	/**
	 * Sets the access level.
	 *
	 * @param accessLevel the new access level
	 */
	@Setter(AccessLevel.PROTECTED)
	private short accessLevel = LEVEL_NORMAL;

	// modules should probably be sorted by priority level in a TreeSet
	/** The priority level. */

	/**
	 * Gets the priority level.
	 * 
	 * @return the priority level
	 */
	@Getter
	/**
	 * Sets the priority level.
	 *
	 * @param priorityLevel the new priority level
	 */
	@Setter(AccessLevel.PROTECTED)
	private short priorityLevel = PRIORITY_LOW;

	/**
	 * Initialize.
	 */
	protected abstract void initialize();

	/**
	 * Gets the formatted table name.
	 * 
	 * @return the formatted table name
	 */
	protected final String getFormattedTableName() {
		return getClass().getSimpleName()
				+ (tableName == null ? "_generic_table" : "_" + getTableName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return isActive ? name : Colors.RED + name + Colors.NORMAL;
	}

	/**
	 * Debug.
	 * 
	 * @param s
	 *            the s
	 */
	protected void debug(String s) {
		if (this.getAccessMode() != ACCESS_DEVELOPMENT)
			return;
		System.err.println(s);
	}

	/**
	 * Gets the target.
	 * 
	 * @param c
	 *            the c
	 * @param u
	 *            the u
	 * @return the target
	 */
	public String getTarget(Channel c, User u) {
		assert (c != null && u != null);
		return c == null ? (u == null ? null : u.getNick()) : c.getName();
	}

	/**
	 * Pass message.
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
	public void passMessage(Bot bot, Channel chan, User user, String message) {
		String target = getTarget(chan, user);
		if (target == null) {
			System.out.println(message);
		} else {
			bot.sendMessage(getTarget(chan, user), message);
			bot.logMessage(chan, user, message);
		}
	}

	/**
	 * Pass emote.
	 * 
	 * @param bot
	 *            the bot
	 * @param chan
	 *            the chan
	 * @param user
	 *            the user
	 * @param emote
	 *            the emote
	 */
	public void passEmote(Bot bot, Channel chan, User user, String emote) {
		String target = getTarget(chan, user);
		if (target == null) {
			System.out.println(emote);
		} else {
			bot.sendAction(getTarget(chan, user), emote);
		}
	}
}
