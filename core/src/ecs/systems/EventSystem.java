package ecs.systems;

import java.util.ArrayList;
import java.util.HashMap;

import ecs.components.CollisionCallback;
import ecs.components.Event;
import ecs.components.EventComponent;
import patterns.EventCallback;
import utility.ImmutableArray;
import utility.Pair;

public class EventSystem {
	public void checkEvents(ImmutableArray<EventComponent> eventComponents) {
		
		HashMap<String, Pair<Event, EventCallback>> publishedEvents = new HashMap<>();
		for (EventComponent eventComp : eventComponents) {
			for (Event event : eventComp.publishedEvents) {
				publishedEvents.put(event.message, new Pair<>(event, eventComp.getObservedCallback()));
			}
		}
		
		for (EventComponent eventComp : eventComponents) {
			for (String s : eventComp.observedEvents) {
				if (publishedEvents.containsKey(s)) {
					Pair<Event, EventCallback> eventOwner = publishedEvents.get(s);
					eventComp.getObservedCallback().onEventObserved(eventOwner.first);
					eventOwner.second.onMyEventObserved(eventOwner.first);
				}
			}
		}
	}
}
