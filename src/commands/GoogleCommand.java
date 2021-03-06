/*
 * @author Rahat Ahmed
 * @description This module returns the result of a Google search
 * @basecmd google
 * @category utility
 */
package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

import com.csam.jentities.HTML4Entities;

// TODO: Auto-generated Javadoc
/**
 * The Class GoogleCommand.
 */
public class GoogleCommand extends Command {

	/** The gdata. */
	private GoogleData gdata;

	/** The entity parser. */
	private HTML4Entities entityParser;

	/**
	 * The Class GoogleData.
	 */
	private class GoogleData {

		/** The results. */
		private ArrayList<String> results;

		/** The position. */
		private int position;

		/**
		 * Sets the results.
		 * 
		 * @param results
		 *            the new results
		 */
		public void setResults(ArrayList<String> results) {
			this.results = results;
		}

		/**
		 * Sets the position.
		 * 
		 * @param position
		 *            the new position
		 */
		public void setPosition(int position) {
			this.position = position;
		}

		/**
		 * Checks for next.
		 * 
		 * @return true, if successful
		 */
		public boolean hasNext() {
			return (results != null && position + 1 < results.size());
		}

		/**
		 * Gets the next.
		 * 
		 * @return the next
		 */
		public String getNext() {
			return results.get(++position);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see modules.Module#initialize()
	 */
	@Override
	protected void initialize() {
		setHelpText("Returns the first search result of a Google query");
		setName("Google");
		addAlias("google");
		addAlias("next");
		gdata = new GoogleData();
		try {
			entityParser = new HTML4Entities();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(backend.Bot, org.pircbotx.Channel,
	 * org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#execute(java.lang.String, backend.Bot,
	 * org.pircbotx.Channel, org.pircbotx.User, java.lang.String)
	 */
	@Override
	public void execute(String alias, Bot bot, Channel chan, User user,
			String message) {
		super.execute(alias, bot, chan, user, message);

		if (alias.equals("google")) {
			if (Util.hasArgs(message, 2)) {
				String query = message.split(" ", 2)[1];
				ArrayList<String> results = makeQuery(query);
				if (results.isEmpty())
					passMessage(bot, chan, user, "No results for: " + query);
				else
					passMessage(bot, chan, user, results.get(0));
			}
		} else if (alias.equals("next")) {
			if (gdata.hasNext())
				passMessage(bot, chan, user, gdata.getNext());
			else
				passMessage(bot, chan, user, "There are no more results.");
		}

	}

	/**
	 * Make query.
	 * 
	 * @param query
	 *            the query
	 * @return the array list
	 */
	private ArrayList<String> makeQuery(String query) {

		ArrayList<String> results = new ArrayList<String>();

		try {
			query = URLEncoder.encode(query, "UTF-8");

			URL url = new URL(
					"http://ajax.googleapis.com/ajax/services/search/web?start=0&rsz=large&v=1.0&q="
							+ query);
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("Referer", "http://www.example.com/");

			// Get the JSON response
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			String response = builder.toString();
			JSONObject json = new JSONObject(response);

			JSONArray ja = json.getJSONObject("responseData").getJSONArray(
					"results");

			for (int i = 0; i < ja.length(); i++) {
				JSONObject j = ja.getJSONObject(i);
				results.add(entityParser.parseText(Colors.BLUE
						+ j.getString("titleNoFormatting") + Colors.NORMAL
						+ " - " + Colors.OLIVE
						+ Util.shorten(j.getString("url"))));
			}

			gdata.setResults(results);
			gdata.setPosition(0);
		} catch (Exception e) {
			// none of these are fatal, I don't care.
			e.printStackTrace();
		}

		return results;
	}

}
