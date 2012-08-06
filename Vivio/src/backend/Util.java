/*
 * @author Kyle Kemp
 */
package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;
import commands.Command;

// TODO: Auto-generated Javadoc
/**
 * The Class Util.
 */
public class Util {

	/**
	 * Gets the args.
	 * 
	 * @param s
	 *            the s
	 * @param args
	 *            the args
	 * @param delim
	 *            the delim
	 * @return the args
	 */
	public static final String[] getArgs(String s, int args, String delim) {
		String[] temp = s.split(delim);
		String[] rVal = new String[args];

		for (int i = 0; i < args; i++) {
			rVal[i] = temp[i];
		}

		rVal[args - 1] = s.substring(s.lastIndexOf(temp[args - 1]));

		return rVal;
	}

	/**
	 * Gets the args.
	 * 
	 * @param s
	 *            the s
	 * @param args
	 *            the args
	 * @return the args
	 */
	public static final String[] getArgs(String s, int args) {
		return getArgs(s, args, " ");
	}

	/**
	 * Checks for args.
	 * 
	 * @param s
	 *            the s
	 * @param args
	 *            the args
	 * @return true, if successful
	 */
	public static boolean hasArgs(String s, int args) {
		return hasArgs(s, args, " ");
	}

	/**
	 * Checks for args.
	 * 
	 * @param s
	 *            the s
	 * @param args
	 *            the args
	 * @param delim
	 *            the delim
	 * @return true, if successful
	 */
	public static boolean hasArgs(String s, int args, String delim) {
		return s.split(delim).length >= args;
	}

	/**
	 * Max args.
	 * 
	 * @param s
	 *            the s
	 * @return the int
	 */
	public static int maxArgs(String s) {
		return s.split(" ").length;
	}

	/**
	 * Format channel.
	 * 
	 * @param s
	 *            the s
	 * @return the string
	 */
	public static String formatChannel(String s) {
		return (s.startsWith("#") || s.startsWith("&")) ? s : "#" + s;
	}

	/**
	 * Checks for link.
	 * 
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	public static boolean hasLink(String s) {
		String[] args = s.split(" ");
		for (String a : args) {
			if (a.matches("[a-z]+://.+?"))
				return true;
		}
		return false;
	}

	/** The goog url. */
	private static String googUrl = "https://www.googleapis.com/urlshortener/v1/url?shortUrl=http://goo.gl/fbsS&key=AIzaSyBpNXaLneyOhwSzristuqzgCZVBbKdWIF8";

	/**
	 * Shorten.
	 * 
	 * @param longUrl
	 *            the long url
	 * @return the string
	 */
	public static String shorten(String longUrl) {
		String shortUrl = "";

		try {
			URLConnection conn = new URL(googUrl).openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write("{\"longUrl\":\"" + longUrl + "\"}");
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;

			while ((line = rd.readLine()) != null) {
				if (line.indexOf("id") > -1) {
					// I'm sure there's a more elegant way of parsing
					// the JSON response, but this is quick/dirty =)
					shortUrl = line.substring(8, line.length() - 2);
					break;
				}
			}

			wr.close();
			rd.close();
		} catch (MalformedURLException ex) {
			return longUrl;
		} catch (IOException ex) {
			return longUrl;
		}

		return shortUrl;
	}

	/**
	 * Extract link.
	 * 
	 * @param link
	 *            the link
	 * @return the string
	 */
	public static String extractLink(String link) {
		if (!link.contains(" "))
			return link;
		if (!link.startsWith("http"))
			link = link.substring(link.indexOf("http"));
		if (link.contains(" "))
			link = link.substring(0, link.indexOf(" "));
		return link;
	}

	/**
	 * Parses the link.
	 * 
	 * @param s
	 *            the s
	 * @return the string
	 */
	public static final String parseLink(String s) {
		String page = "";
		URL url;
		try {
			url = new URL(s);
		} catch (MalformedURLException e) {
			return "";
		}
		URLConnection con;
		try {
			con = url.openConnection();
		} catch (IOException e) {
			return "";
		}
		con.setRequestProperty(
				"User-agent",
				"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.4) Gecko/20100611 Firefox/3.6.4");
		String inputLine;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()))) {
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("<title>")
						&& inputLine.contains("</title>")) {
					page = inputLine.substring(inputLine.indexOf("<title>"));
					break;
				} else if (inputLine.contains("<title>")) {
					page = inputLine.substring(inputLine.indexOf("<title>"));
				} else if (inputLine.contains("</title>")) {
					page += inputLine;
					break;
				} else {
					page += inputLine;
				}
			}
		} catch (IOException e) {
			return "";
		}

		if (page.equals(""))
			return "";

		if (!page.contains("<title>"))
			return "";

		String title = page
				.replaceAll("\n", "")
				.substring(page.indexOf("<title>") + 7,
						page.indexOf("</title>")).trim();
		title = HTMLEntities.unhtmlentities(title);
		return title;
	}

	/**
	 * Gets the elapsed time hours minutes seconds string.
	 * 
	 * @param startTime
	 *            the start time
	 * @return the elapsed time hours minutes seconds string
	 */
	public static String getElapsedTimeHoursMinutesSecondsString(long startTime) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		String format = String.format("%%0%dd", 2);
		elapsedTime = elapsedTime / 1000;
		String seconds = String.format(format, elapsedTime % 60);
		String minutes = String.format(format, (elapsedTime % 3600) / 60);
		String hours = String.format(format, elapsedTime / 3600);
		String time = hours + " hours, " + minutes + " minutes, " + seconds
				+ " seconds";
		return time;
	}

	/**
	 * Checks if the message will invoke a command.
	 * 
	 * @param message
	 *            The message to check
	 * @param bot
	 *            The bot that responds to commands(used to check command
	 *            invocation through the nick command sequence)
	 * @return true if the messsage will invoke a command
	 */
	public static boolean isCommand(String message, Bot bot) {
		return message.startsWith(Command.CMD_SEQUENCE_DEFAULT)
				|| message.startsWith(Command.CMD_SEQUENCE_DEVELOPMENT)
				|| message.startsWith(Command.CMD_SEQUENCE_NORMAL)
				|| message.startsWith(bot.getNick() + ",");
	}
}
