/*
 * @author Rahat Ahmed
 * @description This module starts a new process and kills the current one
 * @basecmd reboot
 * @category core
 */
package commands;

import java.io.IOException;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

public class RebootCommand extends Command
{

	@Override
	protected void initialize()
	{
	setHelpText("Returns the first search result of a Google query");
	setName("Reboot");
	addAlias("reboot");
	
	
	}
	
	@Override
	public void execute(Bot bot, Channel chan, User user, String message)
	{
		
//		String path = RebootCommand.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//		System.out.println("The path is: "+path);
		try
		{
//			Runtime.getRuntime().exec("java -jar " + path);
//			System.exit(0);
			bot.rebootProcess(null);
		} catch (IOException e)
		{
			passMessage(bot, chan, user, "Unable to create new process");
			e.printStackTrace();
		}
	}
	
}
