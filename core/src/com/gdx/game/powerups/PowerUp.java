package com.gdx.game.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ecs.entities.Player;

public abstract class PowerUp {
	private boolean used = false;
	protected float elapsedTimeToFinish;
	public final TextureRegion textureRegion;
	public static final String commonTexture = "Shikashi's Fantasy Icons Pack v2//BG 6.png";
	
	PowerUp(int x, int y, float duration) {
		this.textureRegion = new TextureRegion(new Texture(commonTexture), x * 32, y * 32, 32, 32);
		elapsedTimeToFinish = duration;
	}
	
	public void use(Player player) {
		used = true;
		takeEffect(player);
	}
	
	protected abstract void takeEffect(Player player); 
	
	public void update(Player player, float deltaTime) {
		elapsedTimeToFinish -= deltaTime;
	}
	
	public final boolean isFinished() {
		return elapsedTimeToFinish <= 0.f;
	}
	
	public float getTimeToFinish() {
		return elapsedTimeToFinish;
	}

	public boolean isUsed() {
		return used;
	}
}
