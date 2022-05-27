package screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gdx.game.Gameplay;

import ecs.ComponentDatabase;
import ecs.systems.AudioSystem;
import ecs.systems.EventSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderingSystem;
import patterns.CatchCommand;
import patterns.UsePowerupCommand;
import patterns.WalkCommand;
import utility.CommandMapper;

public class GameScreen implements Screen {
	private RenderingSystem renderingSystem;
	private PhysicsSystem physicsSystem;
	private EventSystem eventSystem;
	private AudioSystem audioSystem;
	private Gameplay gameplay;
	private ComponentDatabase componentDatabase;
	public static BitmapFont font; 
	
	public GameScreen() {
		renderingSystem = new RenderingSystem();
		physicsSystem = new PhysicsSystem();
		eventSystem = new EventSystem();
		audioSystem = new AudioSystem();
		componentDatabase = ComponentDatabase.create();
		font = new BitmapFont();
		
		// Adding commands
		CommandMapper commandMapper = CommandMapper.getInstance();
		commandMapper.addCommand("P1WalkRight", new WalkCommand(WalkCommand.Directions.RIGHT), Input.Keys.RIGHT); 
		commandMapper.addCommand("P1WalkLeft", new WalkCommand(WalkCommand.Directions.LEFT), Input.Keys.LEFT);
		commandMapper.addCommand("P1WalkUp", new WalkCommand(WalkCommand.Directions.UP), Input.Keys.UP);
		commandMapper.addCommand("P1WalkDown", new WalkCommand(WalkCommand.Directions.DOWN), Input.Keys.DOWN);
		commandMapper.addCommand("P1UsePowerUp", new UsePowerupCommand(), Input.Keys.SHIFT_RIGHT);
		commandMapper.addCommand("P1Catch", new CatchCommand("P1"), Input.Keys.ENTER);
		
		commandMapper.addCommand("P2WalkRight", new WalkCommand(WalkCommand.Directions.RIGHT), Input.Keys.D); 
		commandMapper.addCommand("P2WalkLeft", new WalkCommand(WalkCommand.Directions.LEFT), Input.Keys.A);
		commandMapper.addCommand("P2WalkUp", new WalkCommand(WalkCommand.Directions.UP), Input.Keys.W);
		commandMapper.addCommand("P2WalkDown", new WalkCommand(WalkCommand.Directions.DOWN), Input.Keys.S);
		commandMapper.addCommand("P2UsePowerUp", new UsePowerupCommand(), Input.Keys.SHIFT_LEFT);
		commandMapper.addCommand("P2Catch", new CatchCommand("P2"), Input.Keys.SPACE);
		gameplay = new Gameplay(componentDatabase, "Maps//Map.tmx", "Red Curtain.ogg", 60f);
	}
	
	@Override
	public void render(float delta) {
		gameplay.update(eventSystem, delta);
		gameplay.checkCollisions(physicsSystem, delta);
		gameplay.draw(renderingSystem);
		gameplay.playAudio(audioSystem);
	}
	
	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
		gameplay.onScreenResize(width, height);
		renderingSystem.onScreenResize(width, height);
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
		renderingSystem.dispose();
		font.dispose();
		componentDatabase.dispose();
	}

}
