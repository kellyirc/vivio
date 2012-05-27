package modules;

import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;

import backend.Bot;
import backend.Constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


public abstract class Module extends ListenerAdapter<Bot> implements Constants {

	public Module() {
		initialize();
	}
	
	@Getter @Setter(AccessLevel.PROTECTED) private String name;
	
	@Getter @Setter(AccessLevel.PROTECTED)	private String tableName;
	
	@Getter @Setter private boolean isActive = true;
		
	@Getter @Setter(AccessLevel.PROTECTED)	private String helpText = "This is the default help text!";
	
	//whether or not the command is available in the current bots mode.
	@Getter @Setter(AccessLevel.PROTECTED)	private short accessMode = ACCESS_NORMAL;
	
	//the level of access for the module
	@Getter @Setter(AccessLevel.PROTECTED)	private short accessLevel = LEVEL_NORMAL;
	
	//modules should probably be sorted by priority level in a TreeSet
	@Getter @Setter(AccessLevel.PROTECTED)	private short priorityLevel = PRIORITY_LOW;
	
	protected abstract void initialize();
	
	protected final String getFormattedTableName() {
		return getClass().getSimpleName() + (tableName == null ? "_generic_table" : "_"+getTableName());
	}
	
	public String toString() {
		return isActive ? name : Colors.RED + name + Colors.NORMAL;
	}
	
	protected void debug(String s) {
		if(this.getAccessMode() != ACCESS_DEVELOPMENT) return;
		System.err.println(s);
	}
}
