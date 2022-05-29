package com.gdx.game;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import ecs.ComponentDatabase;
import utility.CommandMapper;
import utility.Toolbox;

public abstract class GamePhase implements Disposable {
	protected ComponentDatabase componentDatabase;
	
	public GamePhase() {
		componentDatabase = new ComponentDatabase();
	}
	
	public abstract GamePhase getNewGamePhase(); 
	public abstract void update(float deltaTime);
	public abstract void run(Toolbox toolbox, Viewport viewport, float deltaTime);
	public abstract void setCommands(CommandMapper commandMapper);
	public abstract boolean isFinished();
}
