package console;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.script.ScriptException;

import org.jruby.embed.ScriptingContainer;

public class RubyConsole extends Console
{
	private static final String RESULT_PROMPT = "=> ";
	
	ScriptingContainer jruby;
	ByteArrayOutputStream out;
	
	@Override
	public void initialize()
	{
//		ScriptEngineManager manager = new ScriptEngineManager();
//		jruby = manager.getEngineByName("jruby");
		jruby = new ScriptingContainer();
		resetScriptOutput();
		try
		{
			sendLine("Starting Ruby Console.");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void resetScriptOutput()
	{
		out = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(out);
		jruby.setWriter(writer);
		
//		err = new ByteArrayOutputStream();
//		OutputStreamWriter errWriter = new OutputStreamWriter(err);
//		jruby.setError(errWriter);
	}

	@Override
	public void onLineReceived(String line)
	{
		try
		{
			Object result = jruby.runScriptlet(line);
			
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
