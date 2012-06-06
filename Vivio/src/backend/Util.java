package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

public class Util {
	
	public static final String[] getArgs(String s, int args) {
		String[] temp = s.split(" ");
		String[] rVal = new String[args];

		for (int i = 0; i < args; i++) {
			rVal[i] = temp[i];
		}

		rVal[args - 1] = s.substring(s.lastIndexOf(temp[args - 1]));

		return rVal;
	}

	public static boolean hasArgs(String s, int args) {
		return s.split(" ").length >= args;
	}

	public static int maxArgs(String s) {
		return s.split(" ").length;
	}

	public static String formatChannel(String s) {
		return (s.startsWith("#") || s.startsWith("&")) ? s : "#" + s;
	}

	public static boolean hasLink(String s) {
		return s.contains("http://") || s.contains("https://");
	}
	
	private static String googUrl = "https://www.googleapis.com/urlshortener/v1/url?shortUrl=http://goo.gl/fbsS&key=AIzaSyBpNXaLneyOhwSzristuqzgCZVBbKdWIF8";
	
	public static String shorten(String longUrl)
	{
	    String shortUrl = "";

	    try
	    {
	        URLConnection conn = new URL(googUrl).openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/json");
	        OutputStreamWriter wr =
	                     new OutputStreamWriter(conn.getOutputStream());
	        wr.write("{\"longUrl\":\"" + longUrl + "\"}");
	        wr.flush();

	        // Get the response
	        BufferedReader rd =
	                     new BufferedReader(
	                     new InputStreamReader(conn.getInputStream()));
	        String line;

	        while ((line = rd.readLine()) != null)
	        {
	            if (line.indexOf("id") > -1)
	            {
	                // I'm sure there's a more elegant way of parsing
	                // the JSON response, but this is quick/dirty =)
	                shortUrl = line.substring(8, line.length() - 2);
	                break;
	            }
	        }

	        wr.close();
	        rd.close();
	    }
	    catch (MalformedURLException ex)
	    {
	    	return longUrl;
	    }
	    catch (IOException ex)
	    {
	    	return longUrl;
	    }

	    return shortUrl;
	}

	
	public static String extractLink(String link) {
		if(!link.contains(" ")) return link;
		if(!link.startsWith("http")) 
			link = link.substring(link.indexOf("http"));
		if(link.contains(" "))
			link = link.substring(0, link.indexOf(" "));
		return link;
	}

	public static final String parseLink(String s) throws MalformedURLException,
			IOException {
		String page = "";
		URL url = new URL(s);
		URLConnection con = url.openConnection();
		con.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.4) Gecko/20100611 Firefox/3.6.4");
		String inputLine;
		try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			while((inputLine = in.readLine()) != null) {
				if(inputLine.contains("<title>") && inputLine.contains("</title>")) { 
					page = inputLine.substring(inputLine.indexOf("<title>"));
					break;
				} else if(inputLine.contains("<title>")) {
					page = inputLine.substring(inputLine.indexOf("<title>"));
				} else if(inputLine.contains("</title>")) {
					page += inputLine;
					break;
				} else {
					page += inputLine;
				}
			}
		}
		
		if(page.equals("")) return "";
		
		if(!page.contains("<title>")) return "";
		
		String title = page.replaceAll("\n","").substring(page.indexOf("<title>")+7, page.indexOf("</title>")).trim();
		title = HTMLEntities.unhtmlentities(title);
		return title;
	}
	
}
