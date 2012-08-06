/*
 * @author Kyle Kemp
 */
package backend;

// TODO: Auto-generated Javadoc
/**
 * The Interface Constants.
 */
public interface Constants {

	// access mode: development, normal, disabled -- if the bots mode is
	// equivalent or greater than the commands mode, it's accessible
	/** The Constant ACCESS_DISABLED. */
	public static final short ACCESS_DISABLED = 2;

	/** The Constant ACCESS_DEVELOPMENT. */
	public static final short ACCESS_DEVELOPMENT = 1;

	/** The Constant ACCESS_NORMAL. */
	public static final short ACCESS_NORMAL = 0;

	// access level: owner, op, elevated, normal, banned -- if the person
	// executing the command is >= the access level, the command is executed

	// owner is bot owners
	/** The Constant LEVEL_OWNER. */
	public static final short LEVEL_OWNER = 4;
	// op is channel ops
	/** The Constant LEVEL_OPERATOR. */
	public static final short LEVEL_OPERATOR = 3;
	// elevated is people given special permission on the bot
	/** The Constant LEVEL_ELEVATED. */
	public static final short LEVEL_ELEVATED = 2;
	// normal is for everyone
	/** The Constant LEVEL_NORMAL. */
	public static final short LEVEL_NORMAL = 1;
	// banned is a level that probably won't get used, but is there to allow
	// people to be bot-banned
	/** The Constant LEVEL_BANNED. */
	public static final short LEVEL_BANNED = 0;

	// priority level: high, medium, low -- the commands with the highest
	// priority execute first
	/** The Constant PRIORITY_MODULE. */
	public static final short PRIORITY_MODULE = 3;

	/** The Constant PRIORITY_HIGH. */
	public static final short PRIORITY_HIGH = 2;

	/** The Constant PRIORITY_MEDIUM. */
	public static final short PRIORITY_MEDIUM = 1;

	/** The Constant PRIORITY_LOW. */
	public static final short PRIORITY_LOW = 0;
}
