package commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;
import backend.Util;

public class BrainfuckCommand extends Command {
	private int instructionNumber = 1;
	
	private final int arrayLength = 30000;
	private final int maxLoopIterations = 64 * 1024;
	
	@Override
	protected void initialize() {
		setName("BrainfuckInterpreter");
		setHelpText("Let's interpret some Brainfuck code! ( Wikipedia: http://goo.gl/sEtF )");
		addAlias("brainfuck");
		setUsableInPM(true);
	}
	
	@Override
	protected String format() {
		return super.format() + " [brainfuck code] {input}";
	}
	
	private void debugBF(String command, int position, String text) {
		debugBF(command, position, text, true);
	}
	
	private void debugBF(String command, int position, String text, boolean increasecounter) {
		debug("(" + (increasecounter ? ++instructionNumber : instructionNumber) + ") position: " + position + " '" + command + "' " + text);
	}

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		String target = getTarget(chan, user);
		
		byte bytes[] = new byte[arrayLength];
		int position = 0;
		int currentInput = 0;
		int lastCellValue = 0;
		int loopIterationCount = 0;
		String output = "";
		
		instructionNumber = 0;
	   
		for(int i = 0; i < bytes.length; i++) { bytes[i] = 0; }
	   
		String[] args = null;
	   
		if(Util.hasArgs(message,2)) {
			args = message.split(" ", 3);
			args[1] = args[1].trim();
		   
			for(int i = 0; i < args[1].length(); i++) {
				switch(args[1].charAt(i)) {
					case '>':
						if(++position >= bytes.length) position = 0;
						
						debugBF(""+args[1].charAt(i), i, "array position now " + position);
						break;
					
					case '<':
						if(--position < 0) position = bytes.length-1;
						
						debugBF(""+args[1].charAt(i), i, "array position now " + position);
						break;
						
					case '+':
						bytes[position]++;
						
						debugBF(""+args[1].charAt(i), i, "a[" + position + "] = " + bytes[position]);
						break;
						
					case '-':
						bytes[position]--;
						
						debugBF(""+args[1].charAt(i), i, "a[" + position + "] = " + bytes[position]);
						break;
						
					case '.':
						output += "" + (char)bytes[position];
						
						debugBF(""+args[1].charAt(i), i, "output " + bytes[position] + ": " + (char)bytes[position]);
						break;
						
					case ',':
						if(args.length > 2 && currentInput < args[2].length()) {
							bytes[position] = (byte)args[2].charAt(currentInput++);
							debugBF(""+args[1].charAt(i), i, "read " + bytes[position] + ": " + (char)bytes[position]);
						}
						break;
						
					case '[': {
						int oldi = i;
						
						int startbracketsfound = 0;
						if(bytes[position] == 0) {
							
							while(i < args[1].length()) {
								++i;
								if(args[1].charAt(i) == '[') {
									startbracketsfound++;
								}
								if(args[1].charAt(i) == ']') {
									if(startbracketsfound-- == 0) break;
								}
							}
							
						}
						
						debugBF(""+args[1].charAt(i), oldi, "a[" + position + "] = " + bytes[position]);
						
						break;
					}
					case ']': {
						debugBF(""+args[1].charAt(i), i, "a[" + position + "] = " + bytes[position]);
						
						if(bytes[position] != 0) {
							if(lastCellValue == bytes[position]) {
								if(++loopIterationCount >= maxLoopIterations) {
									passMessage(bot, chan, user, "Suspected infinite loop, stopping execution after " + loopIterationCount + " run(s)...");
									return;
								}
							}
							lastCellValue = bytes[position];
							
							int oldi = i;
							
							int endbracketsfound = 0;
							while(i >= 0) {
								--i;
								if(args[1].charAt(i) == ']') {
									endbracketsfound++;
								}
								if(args[1].charAt(i) == '[') {
									if(endbracketsfound-- == 0) break;
								}
							}
							
							debugBF(""+args[1].charAt(oldi), oldi, "going back to " + i, false);
							--i;
						}
						
						break;
					}
				}
			}
			
			output = output.replace("\n", "[\\n]");
			
			String[] outputs;
			if(!target.equals(user.getNick())) {
				outputs = new String[1];
				outputs[0] = truncateOutput(output);
			}
			else outputs = output.split("(?<=\\G.{370})");
			
			int outputLineNum = 1;
			for(String outputLine : outputs) passMessage(bot, chan, user, "Output (line " + (outputLineNum++) + "): " + outputLine);
		}
		else {
			invalidFormat(bot, chan, user);
		}
	}
	
	private String truncateOutput(String text) {
		if(text.length() > 340) return text.substring(0, 340) + "... (truncated at 340 symbols; use in a PM to get full output)";
		else return text;
	}
}
