package utility;

import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import patterns.commands.Command;

import com.badlogic.gdx.utils.OrderedMap;

public class CommandMapper implements Iterable<Entry<Integer, Command>> {
	private OrderedMap<Integer, Command> commands;
	private static boolean instantiated = false;
	
 	private CommandMapper() {
 		commands = new OrderedMap<Integer, Command>(10);
 	}
 	
	public static CommandMapper create() {
		if (!instantiated) {
			instantiated = true;
			return new CommandMapper();
		}
		
		return null;
	}
	
	public void addCommand(Integer key, Command command) {
		commands.put(key, command);
	}
	
	public void removeAllCommands() {
		commands.clear();
	}
	
	public void removeCommand(Integer key) {
		commands.remove(key);
	}
	
	@Override
	public Entries<Integer, Command> iterator() {
		return commands.iterator();
	}
}
