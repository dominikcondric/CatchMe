package screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.gdx.game.Gameplay;

import ecs.ComponentDatabase;
import ecs.systems.EventSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderingSystem;
import patterns.UsePowerupCommand;
import patterns.WalkCommand;
import utility.CommandMapper;

public class GameScreen implements Screen {
	private RenderingSystem renderingSystem;
	private PhysicsSystem physicsSystem;
	private EventSystem eventSystem;
	private Gameplay currentLevel;
	private ComponentDatabase componentDatabase;
	
	public GameScreen() {
		renderingSystem = new RenderingSystem();
		physicsSystem = new PhysicsSystem();
		eventSystem = new EventSystem();
		componentDatabase = ComponentDatabase.create();
		
		// Adding commands
		CommandMapper commandMapper = CommandMapper.getInstance();
		commandMapper.addCommand("P1WalkRight", new WalkCommand(WalkCommand.Directions.RIGHT, 5.f), Input.Keys.RIGHT); 
		commandMapper.addCommand("P1WalkLeft", new WalkCommand(WalkCommand.Directions.LEFT, 5.f), Input.Keys.LEFT);
		commandMapper.addCommand("P1WalkUp", new WalkCommand(WalkCommand.Directions.UP, 5.f), Input.Keys.UP);
		commandMapper.addCommand("P1WalkDown", new WalkCommand(WalkCommand.Directions.DOWN, 5.f), Input.Keys.DOWN);
		commandMapper.addCommand("P1Interact", new UsePowerupCommand(), Input.Keys.ENTER);
		
		currentLevel = new Gameplay(componentDatabase, "Maps//Map.tmx");
	}
	
	@Override
	public void render(float delta) {
		currentLevel.update(eventSystem, delta);
		currentLevel.checkCollisions(physicsSystem, delta);
		currentLevel.draw(renderingSystem);
	}
	
	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
