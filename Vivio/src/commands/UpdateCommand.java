/*
 * @author Kyle Kemp
 * @description This module lets the bot update itself.
 * @basecmd update
 * @category core
 */
package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

public class UpdateCommand extends Command {
	
	private boolean updating = false;

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {

		if(updating) {
			passMessage(bot, chan, user, "I am already updating. Please be patient.");
			return;
		}
		
		passMessage(bot, chan, user, "I will begin updating now. Please give me some time.");
		
		updating = true;
		
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("./update_vivio.sh > log");
		} catch (IOException e) {
			passMessage(bot, chan, user, e.getMessage());
			return;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()))) {

			String line;
			while ((line = reader.readLine()) != null) {
				passMessage(bot, chan, user, line);
				if(line.equals("Launching new jar.")) {
					bot.disconnect();
					System.exit(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			passMessage(bot, chan, user,
					"That command was not found on this system.");
		}
	}

	@Override
	protected void initialize() {
		setName("Update");
		setHelpText("Run a self-update. I rock like that!");
		addAlias("update");
		setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

}
