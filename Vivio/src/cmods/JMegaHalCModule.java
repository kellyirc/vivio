package cmods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import lombok.Getter;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import backend.Bot;
import backend.Database;
import backend.Util;
import commands.Command;

public class JMegaHalCModule extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		//passMessage(bot,chan,user,"quads size: "+quads.size());
		//passMessage(bot,chan,user,"prev size: "+previous.size());
		//passMessage(bot,chan,user,"next size: "+next.size());
		//passMessage(bot,chan,user,"words size: "+words.size());
		passMessage(bot,chan,user,getSentence());
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent<Bot> event) throws Exception {
		super.onPrivateMessage(event);
		if(Util.isCommand(event.getMessage(), event.getBot())) return;
		add(event.getMessage());
	}

	@Override
	protected void initialize() {
		this.setPriorityLevel(PRIORITY_MODULE);
		this.setHelpText("This lets me learn and talk to you!");
		this.setName("JMegaHAL");
		addAlias("sentence");

		setUsableInPM(true);

		try {
			//don't forget to differentiate between actions and not actions
			//every quad has an id
			//every other table links by quad id
			Database.createTable(
					"jmegahal_quads",
					"q1 char(20), q2 char(20), q3 char(20), q4 char(20)");
			Database.createTable(
					"jmegahal_words",
					"word char(20), quad integer");
			Database.createTable(
					"jmegahal_next",
					"next_word char(20), quad integer");
			Database.createTable(
					"jmegahal_prev",
					"prev_word char(20), quad integer");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public class Quad {

		public Quad(String s1, String s2, String s3, String s4) {
			tokens = new String[] { s1, s2, s3, s4 };
		}

		public String getToken(int index) {
			return tokens[index];
		}

		public void setCanStart(boolean flag) {
			//TODO UPDATE THE DB WITH THIS INFO
			canStart = flag;
		}

		public void setCanEnd(boolean flag) {
			//TODO UPDATE THE DB WITH THIS INFO
			canEnd = flag;
		}

		public int hashCode() {
			return tokens[0].hashCode() + tokens[1].hashCode()
					+ tokens[2].hashCode() + tokens[3].hashCode();
		}

		@Override
		public String toString() {
			return "Quad [tokens=" + Arrays.toString(tokens) + "]";
		}

		public boolean equals(Quad other) {
			return other.tokens[0].equals(tokens[0])
					&& other.tokens[1].equals(tokens[1])
					&& other.tokens[2].equals(tokens[2])
					&& other.tokens[3].equals(tokens[3]);
		}

		private String[] tokens;
		private @Getter boolean canStart = false;
		private @Getter boolean canEnd = false;

	}

	// These are valid chars for words. Anything else is treated as punctuation.
	public static final String WORD_CHARS = "abcdefghijklmnopqrstuvwxyz"
			+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
	public static final String END_CHARS = ".!?";

	/**
	 * Adds an entire documents to the 'brain'. Useful for feeding in stray
	 * theses, but be careful not to put too much in, or you may run out of
	 * memory!
	 */
	public void addDocument(String uri) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new URL(uri).openStream()));
		StringBuffer buffer = new StringBuffer();
		int ch = 0;
		while ((ch = reader.read()) != -1) {
			buffer.append((char) ch);
			if (END_CHARS.indexOf((char) ch) >= 0) {
				String sentence = buffer.toString();
				sentence = sentence.replace('\r', ' ').replace('\n', ' ');
				add(sentence);
				buffer = new StringBuffer();
			}
		}
		add(buffer.toString());
		reader.close();
	}

	/**
	 * Adds a new sentence to the 'brain'
	 */
	public void add(String sentence) {
		sentence = sentence.trim();
		ArrayList<String> parts = new ArrayList<String>();
		char[] chars = sentence.toCharArray();
		int i = 0;
		boolean punctuation = false;
		StringBuffer buffer = new StringBuffer();
		while (i < chars.length) {
			char ch = chars[i];
			if ((WORD_CHARS.indexOf(ch) >= 0) == punctuation) {
				punctuation = !punctuation;
				String token = buffer.toString();
				if (token.length() > 0) {
					parts.add(token);
				}
				buffer = new StringBuffer();
				continue;
			}
			buffer.append(ch);
			i++;
		}
		String lastToken = buffer.toString();
		if (lastToken.length() > 0) {
			parts.add(lastToken);
		}

		if (parts.size() >= 4) {
			for (i = 0; i < parts.size() - 3; i++) {
				Quad quad = new Quad(parts.get(i), parts.get(i + 1),
						parts.get(i + 2), parts.get(i + 3));
				if (quads.containsKey(quad)) {
					quad = quads.get(quad);
				} else {
					selfRef(quad);
				}

				if (i == 0) {
					quad.setCanStart(true);
				}

				if (i == parts.size() - 4) {
					quad.setCanEnd(true);
				}

				for (int n = 0; n < 4; n++) {
					String token = parts.get(i + n);
					if (!words.containsKey(token)) {
						addWord(token, new HashSet<Quad>(1));
					}
					HashSet<Quad> set = words.get(token);
					set.add(quad);
				}

				if (i > 0) {
					String previousToken = parts.get(i - 1);
					if (!previous.containsKey(quad)) {
						addPrev(quad, new HashSet<String>(1));
					}
					HashSet<String> set = previous.get(quad);
					set.add(previousToken);
				}

				if (i < parts.size() - 4) {
					String nextToken = parts.get(i + 4);
					if (!next.containsKey(quad)) {
						addNext(quad, new HashSet<String>(1));
					}
					HashSet<String> set = next.get(quad);
					set.add(nextToken);
				}

			}
		}
	}

	/**
	 * Generate a random sentence from the brain.
	 */
	public String getSentence() {
		return getSentence(null);
	}

	/**
	 * Generate a sentence that includes (if possible) the specified word.
	 */
	public String getSentence(String word) {
		LinkedList<String> parts = new LinkedList<String>();

		Quad[] quads;
		if (words.containsKey(word)) {
			quads = words.get(word).toArray(new Quad[0]);
		} else {
			quads = this.quads.keySet().toArray(new Quad[0]);
		}

		if (quads.length == 0) {
			return "";
		}

		Quad middleQuad = quads[rand.nextInt(quads.length)];
		
		Quad quad = middleQuad;

		for (int i = 0; i < 4; i++) {
			parts.add(quad.getToken(i));
		}
		
		while (!quad.isCanEnd()) {
			String[] nextTokens = next.get(quad).toArray(new String[0]);
			String nextToken = nextTokens[rand.nextInt(nextTokens.length)];
			Quad newQuad = new Quad(quad.getToken(1), quad.getToken(2),
					quad.getToken(3), nextToken);
			selfRef(newQuad);
			quad = this.quads.get(newQuad);
			parts.add(nextToken);
		}

		quad = middleQuad;
		while (!quad.isCanStart()) {
			String[] previousTokens = previous.get(quad).toArray(new String[0]);
			String previousToken = previousTokens[rand
					.nextInt(previousTokens.length)];
			Quad newQuad = new Quad(previousToken, quad.getToken(0),
					quad.getToken(1), quad.getToken(2));
			selfRef(newQuad);
			quad = this.quads.get(newQuad);
			parts.addFirst(previousToken);
		}

		StringBuffer sentence = new StringBuffer();
		Iterator<String> it = parts.iterator();
		while (it.hasNext()) {
			String token = (String) it.next();
			sentence.append(token);
		}

		return sentence.toString();
	}

	public void addWord(String s, HashSet<Quad> h) {
		words.put(s, h);
	}

	// This maps a single word to a HashSet of all the Quads it is in.
	private HashMap<String, HashSet<Quad>> words = new HashMap<String, HashSet<Quad>>();

	
	
	public void selfRef(Quad q) {
		quads.put(q, q);
		try {
			if(!Database.hasRow("select * from jmegahal_quads where q1='"+q.getToken(0)+"' and q2='"+q.getToken(1)+"' and q3='"+q.getToken(2)+"' and q4='"+q.getToken(3)+"'")) {
				//Database.insert("jmegahal_quads", "q1,q2,q3,q4","'"+q.getToken(0)+"','"+q.getToken(1)+"','"+q.getToken(2)+"','"+q.getToken(3)+"'");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// A self-referential HashMap of Quads.
	private HashMap<Quad, Quad> quads = new HashMap<Quad, Quad>();

	
	
	public void addNext(Quad q, HashSet<String> s) {
		next.put(q, s);
	}

	// This maps a Quad onto a Set of Strings that may come next.
	private HashMap<Quad, HashSet<String>> next = new HashMap<Quad, HashSet<String>>();

	
	
	public void addPrev(Quad q, HashSet<String> s) {
		previous.put(q, s);
	}

	// This maps a Quad onto a Set of Strings that may come before it.
	private HashMap<Quad, HashSet<String>> previous = new HashMap<Quad, HashSet<String>>();

	
	
	
	private Random rand = new Random();

}
