package ecs;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import ecs.components.AnimationComponent;
import ecs.components.Component;
import ecs.components.EventComponent;
import ecs.components.LightComponent;
import ecs.components.PhysicsComponent;
import ecs.components.SpriteComponent;
import utility.ImmutableArray;

public class ComponentDatabase {
	private ObjectMap<Class<? extends Component>, Array<? extends Component>> database;
	private static boolean instantiated = false;
	
	public static ComponentDatabase create() {
		if (!instantiated) {
			instantiated = true;
			return new ComponentDatabase();
		}
		
		return null;
	}
	
	private ComponentDatabase() {
		database = new ObjectMap<>();
		database.put(SpriteComponent.class, new Array<SpriteComponent>());
		database.put(PhysicsComponent.class, new Array<PhysicsComponent>());
		database.put(AnimationComponent.class, new Array<AnimationComponent>());
		database.put(LightComponent.class, new Array<LightComponent>());
		database.put(EventComponent.class, new Array<EventComponent>());
	}
	
	public <T extends Component> void addComponent(T component) {
		@SuppressWarnings("unchecked")
		Array<T> array = (Array<T>) database.get(component.getClass());
		if (!array.contains(component, true))
			array.add(component);
	}
	
	public <T extends Component> void removeComponent(T component) {
		@SuppressWarnings("unchecked")
		Array<T> array = (Array<T>) database.get(component.getClass());
		array.removeValue(component, true);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> ImmutableArray<T> getComponentArray(Class<T> type) {
		return new ImmutableArray<T>((Array<T>) database.get(type));
	}
}
