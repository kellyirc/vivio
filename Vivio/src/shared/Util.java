package shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

	public static boolean checkArgs(String s, int args) {
		return s.split(" ").length >= args;
	}

	public static int maxArgs(String s) {
		return s.split(" ").length;
	}

	public static String formatChannel(String s) {
		return (s.startsWith("#") || s.startsWith("&")) ? s : "#" + s;
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
	
}
