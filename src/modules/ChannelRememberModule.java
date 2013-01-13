package modules;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PartEvent;

import backend.Bot;
import backend.Database;

// TODO: Auto-generated Javadoc
/**
 * The Class ChannelRememberModule.
 */
public class ChannelRememberModule extends Module {

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		setPriorityLevel(PRIORITY_MODULE);
		setName("ChannelRemember");
		setHelpText("This helps me remember where I was before I disconnected!");
		setTableName("channels");

		try {
			Database.createTable(this.getFormattedTableName(),
					"server char(20), channel char(30)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onConnect(org.pircbotx.hooks.events
	 * .ConnectEvent)
	 */
	@Override
	public void onConnect(ConnectEvent<Bot> event) throws Exception {
		List<HashMap<String, Object>> rows = Database.select("select * from "
				+ getFormattedTableName() + " where server='"
				+ event.getBot().getServer() + "'");
		for (HashMap<String, Object> row : rows) {
			event.getBot().joinChannel((String) row.get("CHANNEL"));
		}
		super.onConnect(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onJoin(org.pircbotx.hooks.events.JoinEvent
	 * )
	 */
	@Override
	public void onJoin(JoinEvent<Bot> event) throws Exception {
		if (event.getUser().equals(event.getBot().getUserBot())
				&& !Database.hasRow("select * from " + getFormattedTableName()
						+ " where server='" + event.getBot().getServer()
						+ "' and channel='" + event.getChannel().getName()
						+ "'")) {
			Database.insert(getFormattedTableName(), "server,channel",
					new String[] { event.getBot().getServer(),
							event.getChannel().getName() }, new boolean[] {
							true, true });
		}
		super.onJoin(event);
	}

	// TODO figure out why this isn't being called.
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onPart(org.pircbotx.hooks.events.PartEvent
	 * )
	 */
	@Override
	public void onPart(PartEvent<Bot> event) throws Exception {
		super.onPart(event);
	}

}
