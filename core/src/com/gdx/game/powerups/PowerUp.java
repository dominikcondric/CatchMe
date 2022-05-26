package com.gdx.game.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ecs.entities.Player;

public abstract class PowerUp {
	protected float elapsedTimeToFinish;
	public final TextureRegion textureRegion;
	public static final String commonTexture = "Shikashi's Fantasy Icons Pack v2//BG 6.png";
	
	PowerUp(int x, int y, float duration) {
		this.textureRegion = new TextureRegion(new Texture(commonTexture), x * 32, y * 32, 32, 32);
		elapsedTimeToFinish = duration;
	}
	
	public abstract void use(Player player);
	
	public void update(Player player, float deltaTime) {
		elapsedTimeToFinish -= deltaTime;
	}
	
	public final boolean isFinished() {
		return elapsedTimeToFinish <= 0.f;
	}
}
