package listeners;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import lombok.Getter;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;

import bot.Bot;

public class ListenerBuilder {
	
	//we want a ListenerManager to be shared by all of the Bot instances.
	
	@Getter
	static ListenerManager<Bot> manager = new ThreadedListenerManager<>();
	
	static {
		manager.addListener(new GenericListener());
	}
	
	private static class GenericListener extends ListenerAdapter<Bot> {

		@Override
		public void onEvent(Event<Bot> arg0) throws Exception {
			super.onEvent(arg0);
			
			//search through all methods
			for(Method m : this.getClass().getMethods()) {
				
				//look for a method header that has the given event as an arg
				for(Type t : m.getGenericParameterTypes()) {
					boolean nameEquals = t.toString().substring(0, t.toString().length()-3).equals(arg0.getClass().getName());

					//get the method name
					if(nameEquals) {

						//invoke that on all scripts
						arg0.getBot().invokeAll(m.getName(), new Object[]{arg0});
						return;
					}
				}
			}
		}

		/* (non-Javadoc)
		 * @see org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events.MessageEvent)
		 */
		@Override
		public void onMessage(MessageEvent<Bot> event) throws Exception {
			super.onMessage(event);
			event.getBot().invokeAll("onMessage", new Object[]{event});
			event.getBot().checkCommands(event.getUser(), event.getMessage(), event.getChannel());
		}

		/* (non-Javadoc)
		 * @see org.pircbotx.hooks.ListenerAdapter#onNotice(org.pircbotx.hooks.events.NoticeEvent)
		 */
		@Override
		public void onNotice(NoticeEvent<Bot> event) throws Exception {
			event.getBot().invokeAll("onNotice", new Object[]{event});
			event.getBot().checkCommands(event.getUser(), event.getMessage(), null);
			super.onNotice(event);
		}

		/* (non-Javadoc)
		 * @see org.pircbotx.hooks.ListenerAdapter#onPrivateMessage(org.pircbotx.hooks.events.PrivateMessageEvent)
		 */
		@Override
		public void onPrivateMessage(PrivateMessageEvent<Bot> event)
				throws Exception {
			event.getBot().invokeAll("onPrivateMessage", new Object[]{event});
			event.getBot().checkCommands(event.getUser(), event.getMessage(), null);
			super.onPrivateMessage(event);
		}
		
	}
}
