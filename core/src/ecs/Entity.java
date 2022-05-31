package ecs;

import com.badlogic.gdx.utils.ObjectMap;

import ecs.components.Component;

public abstract class Entity {
	private ComponentDatabase componentDatabase;
	private ObjectMap<Class<? extends Component>, Component> components;
	protected boolean destroy = false;
	
	public Entity(ComponentDatabase componentDB) {
		components = new ObjectMap<>();
		componentDatabase = componentDB;
	}
	
	public abstract void update(float deltaTime);
	
	final protected <T extends Component> void addComponent(T component) {
		if (!hasComponent(component.getClass())) {
			components.put(component.getClass(), component);
			componentDatabase.addComponent(component);
		}
	}
	
	@SuppressWarnings("unchecked")
	final public <T extends Component> T getComponent(final Class<T> componentType) {
		if (components.containsKey(componentType)) {
			return (T) components.get(componentType);
		}
		
		return null;
	}
	
	final public <T extends Component> boolean hasComponent(Class<T> componentType) {
		if (components.containsKey(componentType)) {
			return true;
		}
		
		return false;
	}
	
	public final boolean shouldDestroy() {
		if (destroy) {
			for (ObjectMap.Entry<Class<? extends Component>, Component> entry : components) {
				componentDatabase.removeComponent(entry.key.cast(entry.value));
			}
		}

		return destroy;
	}
}
