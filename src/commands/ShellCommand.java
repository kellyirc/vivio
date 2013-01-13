/*
 * @author Kyle Kemp
 * @description This module lets you run shell commands directly from irc.
 * @basecmd shell
 * @category utility
 */
package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

public class ShellCommand extends Command {

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if (!Util.hasArgs(message, 2)) {
			invalidFormat(bot, chan, user);
			return;
		}

		String[] args = Util.getArgs(message, 2);

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(args[1]);
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			passMessage(bot, chan, user, e.getMessage());
			return;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()))) {

			String line;
			while ((line = reader.readLine()) != null) {
				passMessage(bot, chan, user, line);
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
		setName("ShellAccess");
		setHelpText("Run a low-level shell command. Oh baby ;)");
		addAlias("shell");
		setAccessLevel(LEVEL_OWNER);
		setUsableInPM(true);
	}

}
