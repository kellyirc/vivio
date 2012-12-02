package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import lombok.Getter;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

import backend.Bot;

import com.google.gson.Gson;

public class RhymeCommand extends Command {
	@Override
	protected void initialize() {
		setName("RhymeFinder");
		setHelpText("Finds one or more rhymes for a word! (" +
				Colors.UNDERLINE + Colors.BOLD + "these" + Colors.NORMAL + " rhymes are best rhymes) " +
				"(Powered by http://rhymebrain.com/en)");
		addAlias("rhyme");
	}
	
	@Override
	protected String format() {
		return super.format() + " [word]";
	}

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		String[] messageParts = message.split(" ", 2);
		
		Rhyme[] rhymes = getRhymes(messageParts[1], 12);
		
		StringBuilder rhymeStringBuilder = new StringBuilder();
		for (int i = 0; i < rhymes.length; i++) {
			Rhyme rhyme = rhymes[i];
			
			if(i != 0) rhymeStringBuilder.append(", ");
			
			if(rhyme.score >= 300)
				rhymeStringBuilder.append(Colors.UNDERLINE + Colors.BOLD).append(rhyme.word).append(Colors.NORMAL);
			
			else rhymeStringBuilder.append(rhyme.word);
		}
		
		passMessage(bot, chan, user, "Found rhymes: " + rhymeStringBuilder.toString());
	}
	
	public Rhyme[] getRhymes(String word, int maxResults) {
		try {
			word = URLEncoder.encode(word, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String replyJson;
		
		try {
			URL requestUrl =
					new URL(String.format("http://rhymebrain.com/talk?function=getRhymes&word=%s&maxResults=%d",
							word, maxResults));
			URLConnection conn = requestUrl.openConnection();
			InputStream in = conn.getInputStream();
			
			String line;
			StringBuilder replyBuilder = new StringBuilder();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while ((line = reader.readLine()) != null) {
				replyBuilder.append(line);
			}
			
			replyJson = replyBuilder.toString();
			
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Gson gson = new Gson();
		
		return gson.fromJson(replyJson, Rhyme[].class);
	}
	
	public static class Rhyme {
		@Getter private String word;
		@Getter private int score;
		@Getter private String flags;
		@Getter private int syllables;
		@Getter private int freq;
	}
}
