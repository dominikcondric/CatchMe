package patterns;

import ecs.components.Event;

public interface EventCallback {
	public void onEventObserved(Event event);
	public void onMyEventObserved(Event event);
}
