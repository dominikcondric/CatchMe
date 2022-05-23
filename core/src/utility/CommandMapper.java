package utility;

import com.badlogic.gdx.utils.OrderedMap;
import patterns.Command;

public class CommandMapper {
	private static CommandMapper instance;
	private OrderedMap<String, CommandMap> commands;
	
 	private CommandMapper() {
 		commands = new OrderedMap<String, CommandMap>(2);
 	}
 	
 	public class CommandMap {
 		public CommandMap(Integer key, Command command) {
			this.key = key;
			this.command = command;
		}
 		
		private Integer key;
 		private Command command;
 		
 		public Integer getKey() { return key; }
 		public Command getCommand() { return command; }
 	}
 	
	
	public static CommandMapper getInstance() {
		if (instance == null)
			instance = new CommandMapper();
		
		return instance;
	}
	
	public void addCommand(String commandName, Command command, Integer key) {
		commands.put(commandName, new CommandMap(key, command));
	}
	
	public void removeCommand(String commandName) {
		commands.remove(commandName);
	}
	
	public CommandMap getCommandKey(String commandName) {
		return commands.get(commandName);
	}
}
