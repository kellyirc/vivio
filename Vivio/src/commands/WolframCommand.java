package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

public class WolframCommand extends Command
{

	static WAEngine engine;
	
	@Override
	protected void initialize()
	{
		engine = new WAEngine();
		engine.setAppID("UY6XJ8-22G848H5YA");
        engine.addFormat("plaintext");
        
        setHelpText("Sends a call to the Wolfram Alpha API. Outputs only the result, if there is one. If you want to read the full result, use !wolfram in a private message.");
		setName("Wolfram");
		addAlias("wolfram");

	}
	
	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
		if(Util.hasArgs(message, 2))
		{
			// Create the query.
	        WAQuery query = engine.createQuery();
	        // Set properties of the query.
	        query.setInput(message.split(" ",2)[1]);
	        
	        try {
	            // This sends the URL to the Wolfram|Alpha server, gets the XML result
	            // and parses it into an object hierarchy held by the WAQueryResult object.
	            WAQueryResult queryResult = engine.performQuery(query);
	            String result = "";
	        	String PMResult = "";
	            if (queryResult.isError()) {
	            	result = "Query error, error code: " + queryResult.getErrorCode() + ", error message: " + queryResult.getErrorMessage();
	            	PMResult = result;
	            } else if (!queryResult.isSuccess()) {
	                result = "Query was not understood; no results available.";
	                PMResult = result;
	            } else {
	                // Got a result.
	            	
		                for (WAPod pod : queryResult.getPods()) {
		                    if (!pod.isError()) {
		                        PMResult += pod.getTitle()+": ";
		                    if (pod.getID().equalsIgnoreCase("result") || pod.getID().equalsIgnoreCase("solution") || pod.getID().equalsIgnoreCase("DecimalApproximation"))
		                    	result += pod.getTitle() + ": ";
		                        for (WASubpod subpod : pod.getSubpods()) {
		                            for (Object element : subpod.getContents()) {
		                                if (element instanceof WAPlainText) {
		                                    PMResult += (((WAPlainText) element).getText()) + ", ";
		                                    if (pod.getID().equalsIgnoreCase("result") || pod.getID().equalsIgnoreCase("solution") || pod.getID().equalsIgnoreCase("DecimalApproximation"))
		                                    {
		                                    	result += (((WAPlainText) element).getText()) + ", ";
		                                    }
		                                    
		                                }
		                            }
		                        }
		                        PMResult += "\n";
		                    }
		                }
		                if(!result.isEmpty())
		                {
		                	if(!getTarget(chan, user).equals(user.getNick()))
			                	for(String s:result.split("\n"))
			                		passMessage(bot, chan, user, s);
		                	else
		                		for(String s:PMResult.split("\n"))
		                			passMessage(bot, chan, user, s);
		                }
	            	}
	        
	        } catch (WAException e) {
	        	
	        }
		}
		else
			invalidFormat(bot, chan, user);
	}
	
	@Override
	protected String format()
	{
		return super.format() + " [query]";
	}

}
