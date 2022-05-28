package ecs;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import ecs.components.AnimationComponent;
import ecs.components.Component;
import ecs.components.EventComponent;
import ecs.components.GuiComponent;
import ecs.components.LightComponent;
import ecs.components.PhysicsComponent;
import ecs.components.SoundComponent;
import ecs.components.SpriteComponent;
import utility.ImmutableArray;

public class ComponentDatabase implements Disposable {
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
		database.put(GuiComponent.class, new Array<GuiComponent>());
		database.put(SoundComponent.class, new Array<SoundComponent>());
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

	@Override
	public void dispose() {
		for (SoundComponent soundComp : getComponentArray(SoundComponent.class)) {
			soundComp.dispose();
		}
		
		for (SpriteComponent spriteComp : getComponentArray(SpriteComponent.class)) {
			spriteComp.dispose();
		}
		
		for (AnimationComponent animationComp : getComponentArray(AnimationComponent.class)) {
			animationComp.dispose();
		}
		
		for (GuiComponent guiComp : getComponentArray(GuiComponent.class)) {
			guiComp.dispose();
		}
		
	}
}
