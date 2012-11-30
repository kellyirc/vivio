package console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JSConsole extends Console
{
	private static final String RESULT_PROMPT = "=> ";

	ScriptEngine js;
	ByteArrayOutputStream out;
	@Override
	public void initialize()
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		js = manager.getEngineByName("JavaScript");
		resetScriptOutput();
		try
		{
			sendLine("Starting JavaScript Console.");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void resetScriptOutput()
	{
		out = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(out);
		js.getContext().setWriter(writer);
	}
	
	@Override
	public void onLineReceived(String line)
	{
		
		try
		{
			Object result = js.eval(line);
			
			String output = out.toString();
			System.out.println(output);
			if(output != null && !output.isEmpty())
			{
				sendLine(output.trim());
				resetScriptOutput();

			}

			sendLine(RESULT_PROMPT+result);
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (Exception e) {
			try
			{
				sendLine(e.toString());
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		
	}

}
