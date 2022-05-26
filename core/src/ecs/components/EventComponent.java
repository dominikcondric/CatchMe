package ecs.components;

import com.badlogic.gdx.utils.Array;

import patterns.Event;
import patterns.EventCallback;


public class EventComponent implements Component {
	public Array<String> observedEvents;
	public Array<Event> publishedEvents;
	private EventCallback observedCallback;
	
	public EventComponent(EventCallback callback) {
		observedEvents = new Array<>();
		publishedEvents = new Array<>();
		this.observedCallback = callback; 
	}
	
	public EventCallback getObservedCallback() {
		return observedCallback;
	}
}
