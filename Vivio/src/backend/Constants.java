package backend;

public interface Constants {
	
	//access mode: development, normal, disabled -- if the bots mode is equivalent or greater than the commands mode, it's accessible
	public static final short ACCESS_DEVELOPMENT = 2;
	public static final short ACCESS_NORMAL = 1;
	public static final short ACCESS_DISABLED = 0;

	//access level: owner, op, elevated, normal, banned -- if the person executing the command is >= the access level, the command is executed
	
	//owner is bot owners
	public static final short LEVEL_OWNER = 4;
	//op is channel ops
	public static final short LEVEL_OPERATOR = 3;
	//elevated is people given special permission on the bot
	public static final short LEVEL_ELEVATED = 2;
	//normal is for everyone
	public static final short LEVEL_NORMAL = 1;
	//banned is a level that probably won't get used, but is there to allow people to be bot-banned
	public static final short LEVEL_BANNED = 0;

	//priority level: high, medium, low -- the commands with the highest priority execute first
	public static final short PRIORITY_MODULE = 3;
	public static final short PRIORITY_HIGH = 2;
	public static final short PRIORITY_MEDIUM = 1;
	public static final short PRIORITY_LOW = 0;
}
