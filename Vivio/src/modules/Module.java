package modules;

import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;

import bot.Bot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import shared.Constants;

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
	
	protected void initialize() {
		setPriorityLevel(PRIORITY_MODULE);
	}
	
	protected String getFormattedTableName() {
		return getClass().getSimpleName() + tableName == null ? "_generic_table" : getTableName();
	}
	
	public String toString() {
		return isActive ? name : Colors.RED + name + Colors.NORMAL;
	}
	
		//create tables here
		//load data here
		
		/*
		something like:
		
		create if not exists table (id, var1, var2, var3);
		select * from table;
		
		foreach(data as x=>y)
			map.set(x, y);
		 */
}
