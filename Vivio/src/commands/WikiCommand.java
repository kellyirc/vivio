package commands;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.types.Commandline.Argument;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import backend.Bot;
import backend.Util;

import com.csam.jentities.HTML4Entities;

public class WikiCommand extends Command
{
	
//	Wiki wiki;
	
	HTML4Entities entityParser;
	
	@Override
	protected void initialize()
	{
		setHelpText("Get the first 512 characters of a Wikipedia page.");
		setName("Wikipedia");
		addAlias("wiki");
		setUsableInPM(true);
	
		try
		{
			entityParser = new HTML4Entities();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
//		wiki = new Wiki("en.wikipedia.org");
		
		
	}
	
	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
		if(Util.hasArgs(message, 2))
		{
			String[] args = message.split(" ", 2);
			passMessage(bot, chan, user, getWikiIntro(args[1]));
		}
		else
		{
			invalidFormat(bot, chan, user);
		}
		
	}
	
	@Override
	protected String format()
	{
		return super.format();
	}
	private String getWikiIntro(String title)
	{
		try
		{
			title = URLEncoder.encode(title, "UTF-8");

			URL url = new URL("http://en.wikipedia.org/w/api.php?action=parse&prop=text&format=xml&page="+title);
			URLConnection con = url.openConnection();
			InputStream in =  con.getInputStream();
			StringBuilder builder = new StringBuilder();
			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
			
			String raw = entityParser.parseText(builder.toString());
			raw = raw.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.1\"?>");
			//convert to bold
			raw = raw.replaceAll("(?i)</?B>", "&#x2;");
			raw = raw.replaceAll("(?i)</?U>", "&#x1F;");
			raw = raw.replaceAll("(?i)</?I>", "&#x16;");
			
			Element root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(raw.getBytes())).getDocumentElement();
			NodeList paragraphs = root.getElementsByTagName("p");
			System.out.println(paragraphs.getLength());
			if(paragraphs.getLength() < 1)
				throw new IllegalArgumentException();
			String text = paragraphs.item(0).getTextContent();
			return text.substring(0, Math.min(300,text.length())) + ((300<text.length())?"...":"");
		} catch (IOException | SAXException | ParserConfigurationException e)
		{
			return "Unable to get page text.";
		} catch(IllegalArgumentException e)
		{
			return "That page doesn't exist.";
		}
		
	}
}
