package com.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.viewport.FitViewport;

import ecs.systems.AudioSystem;
import ecs.systems.EventSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderingSystem;
import patterns.commands.Command;
import utility.CommandMapper;
import utility.Toolbox;

public class CatchMe extends ApplicationAdapter {
	private RenderingSystem renderingSystem;
	private PhysicsSystem physicsSystem;
	private EventSystem eventSystem;
	private AudioSystem audioSystem;
	public static BitmapFont font; 
	private FitViewport viewport;
	private CommandMapper commandMapper;
	private GamePhase gamePhase = null;
	private Toolbox toolbox;
	
	@Override
	public void create() {
		renderingSystem = new RenderingSystem();
		physicsSystem = new PhysicsSystem();
		eventSystem = new EventSystem();
		audioSystem = new AudioSystem();
		font = new BitmapFont();
		commandMapper = CommandMapper.create();
		
		toolbox = new Toolbox(renderingSystem, physicsSystem, audioSystem, eventSystem);
		
		final float aspectRatio = Gdx.graphics.getWidth() / 2.f / Gdx.graphics.getHeight();
		viewport = new FitViewport(aspectRatio * 15.f, 15.f, new OrthographicCamera());
		viewport.setScreenBounds(0, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
		viewport.getCamera().position.x = 20f;
		viewport.getCamera().position.y = 20f;
		viewport.getCamera().update();
		
		gamePhase = new GameSetupPhase();
		gamePhase.setCommands(commandMapper);
	}

	private void update() {
		if (gamePhase.isFinished()) {
			commandMapper.removeAllCommands();
			gamePhase.dispose();
			gamePhase = gamePhase.getNewGamePhase();
			gamePhase.setCommands(commandMapper);
			gamePhase.update(Gdx.graphics.getDeltaTime());
		} else {
			gamePhase.update(Gdx.graphics.getDeltaTime());
		}
	}
	
	@Override
	public void render() {
		update();
		gamePhase.run(toolbox, viewport, Gdx.graphics.getDeltaTime());
		executeCommands();
	}
	
	private void executeCommands() {
		for (Entry<Integer, Command> commandMap : commandMapper) {
			if ((commandMap.value.isSinglePress() && Gdx.input.isKeyJustPressed(commandMap.key))
					|| (!commandMap.value.isSinglePress() && Gdx.input.isKeyPressed(commandMap.key))) {
				commandMap.value.execute(Gdx.graphics.getDeltaTime());
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.setScreenSize(width / 2, height);
	}

	@Override
	public void dispose() {
		renderingSystem.dispose();
		font.dispose();
	}
}
