package utility;

import ecs.systems.AudioSystem;
import ecs.systems.EventSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderingSystem;

public class Toolbox {
	private RenderingSystem renderingSystem;
	private PhysicsSystem physicsSystem;
	private AudioSystem audioSystem;
	private EventSystem eventSystem;
	
	public Toolbox(RenderingSystem rs, PhysicsSystem ps, AudioSystem as, EventSystem es) {
		renderingSystem = rs;
		physicsSystem = ps;
		eventSystem = es;
		audioSystem = as;
	}

	public RenderingSystem getRenderingSystem() {
		return renderingSystem;
	}

	public PhysicsSystem getPhysicsSystem() {
		return physicsSystem;
	}

	public AudioSystem getAudioSystem() {
		return audioSystem;
	}

	public EventSystem getEventSystem() {
		return eventSystem;
	}
}
