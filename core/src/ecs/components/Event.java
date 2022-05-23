package ecs.components;

public class Event {
	public final String message;
	public final Object data;
	
	public Event(String message, Object data) {
		this.message = message;
		this.data = data;
	}

	@Override
	public int hashCode() {
		return message.hashCode();
	}
}
