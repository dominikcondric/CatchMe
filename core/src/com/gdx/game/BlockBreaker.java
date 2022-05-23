package com.gdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import screens.GameScreen;

public class BlockBreaker extends Game {
	@Override
	public void create() {
		this.setScreen(new GameScreen());
	}
	
	@Override
	public void render () {
		super.render();
	}
}
