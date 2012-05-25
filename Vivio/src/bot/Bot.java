package bot;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import listeners.ListenerBuilder;
import lombok.Getter;
import lombok.Setter;
import modules.Module;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.exception.IrcException;

import commands.*;

import shared.Constants;

public class Bot extends PircBotX implements Constants{
	
	static {
		//TODO load owners, elevated, banned from the database
	}
	
	//All of the Bot instances
	@Getter private static LinkedList<Bot> bots = new LinkedList<>();
	@Getter private static HashSet<String> owners = new HashSet<>();
	@Getter private static HashSet<String> elevated = new HashSet<>();
	@Getter private static HashSet<String> banned = new HashSet<>();

	//Constants
	final static String INTERNAL_VERSION = "0.01";
	final static String DEFAULT_SERVER = "irc.esper.net";
	final static int DEFAULT_PORT = 6667;
	
	//Variables 
	@Getter @Setter private boolean parsesSelf = false;
	@Getter @Setter private int botMode = ACCESS_DEVELOPMENT;
	
	//Module comparator
	private class ModComparator implements Comparator<Module> {

		@Override
		public int compare(Module o1, Module o2) {
			if(o2.getPriorityLevel() - o1.getPriorityLevel() == 0) return o1.getName().compareTo(o2.getName());
			return o2.getPriorityLevel() - o1.getPriorityLevel();
		}
		
	}
	
	@Getter
	private CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();
	
	//Constructors
	public Bot() {
		this(DEFAULT_SERVER);
	}

	public Bot(String server) {
		this(server, DEFAULT_PORT);
	}

	public Bot(String server, int port) {
		this(server, port, false);
	}

	public Bot(String server, int port, boolean SSL) {

		initialize();
		
		connectToServer(server, port, SSL);
		
		//TODO: this belongs in a module
		this.joinChannel("#blargity");
		this.joinChannel("#kellyirc");
		
	}

	//Methods
	private void connectToServer(String server, int port, boolean SSL) {
		
		this.setAutoNickChange(true);
		this.setVerbose(true);
		this.setAutoSplitMessage(true);

		this.setVersion("PircBotX~Vivio v" + INTERNAL_VERSION);
		this.setLogin("VivioBot");
		this.setName("VivioBot");

		try {
			if (SSL)
				this.connect(server, port,
						new UtilSSLSocketFactory().trustAllCertificates());
			else
				this.connect(server, port);
		} catch (IOException | IrcException e) {
			e.printStackTrace();
		}
	}

	//initialize the bot
	private void initialize() {
		bots.add(this);
		this.setListenerManager(ListenerBuilder.getManager());
		addModule(new TestCommand());
		addModule(new QuietCommand());
		addModule(new ListCommand());
		addModule(new ToggleCommand());
	}
	
	//get the default bot -- this will almost always be the proper one to get.
	public static Bot getBot() {
		return bots.getFirst();
	}
	
	//invoke a specific method on all of the commands and modules
	public void invokeAll(String method, Object[] args) {
		for(Module m : modules) {
			
			//invoke methods onFoo
			for(Method methodName : m.getClass().getMethods()) {
				if(methodName.getName().equals(method)) {
					try {
						methodName.invoke(m, args);
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
	}

	//parse commands coming in
	public boolean checkCommands(User user, String message, Channel chan) {
		
		String commandString = message.split(" ")[0];
	
		String comm = commandString.substring(1);
		String prefix = commandString.substring(0,1);
				
		for(Module m : modules) {
			if(m instanceof Command) {
				Command command = (Command) m;
				if(!command.isActive()) continue;
				if(botMode < command.getAccessMode()) continue;
				if(getLevelForUser(user, chan) < command.getAccessLevel()) continue;
				if(!prefix.equals(command.getCmdSequence())) continue;
				if(!command.getAliases().contains(comm)) continue;
				command.execute(comm, this, chan, user, message);
				if(command.isStopsExecution()) return false;
			}
		}
		return true;
	}
	
	private void addModule(Module m) {
		this.modules.add(m);
		List<Module> modules = Arrays.asList(this.modules.toArray(new Module[0]));
		Collections.sort(modules, new ModComparator());
		this.modules.clear();
		this.modules.addAll(modules);
	}

	@Override
	public void sendAction(String target, String action) {
		if(banned.contains(target)) return;
		super.sendAction(target, action);
	}

	@Override
	public void sendMessage(String target, String message) {
		if(banned.contains(target)) return;
		super.sendMessage(target, message);
	}

	@Override
	public void sendNotice(String target, String notice) {
		if(banned.contains(target)) return;
		super.sendNotice(target, notice);
	}

	private int getLevelForUser(User u, Channel c) {
		if(c == null) {
			if(owners.contains(u.getNick())) return LEVEL_OWNER;
			if(elevated.contains(u.getNick())) return LEVEL_ELEVATED;
			if(banned.contains(u.getNick())) return LEVEL_BANNED;
		} else if(c.isOp(u)) return LEVEL_OPERATOR;
		return LEVEL_NORMAL;
	}
	
	public static void addBanned(String bnd) {
		banned.add(bnd);
	}
	
	public static void removeBanned(String bnd) {
		banned.remove(bnd);
	}
	
	public static void addOwner(String bnd) {
		owners.add(bnd);
	}
	
	public static void removeOwner(String bnd) {
		owners.remove(bnd);
	}
	
	public static void addElevated(String bnd) {
		elevated.add(bnd);
	}
	
	public static void removeElevated(String bnd) {
		elevated.remove(bnd);
	}
}
