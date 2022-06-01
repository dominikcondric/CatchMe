package com.gdx.game.powerups;

import ecs.components.LightComponent;
import ecs.entities.Player;

public class LightPowerUp extends PowerUp {

	public LightPowerUp() {
		super(9, 10, 10f);
	}

	@Override
	protected void takeEffect(Player player) {
		LightComponent lightComp = player.getComponent(LightComponent.class);
		lightComp.setRadius(lightComp.getRadius() * 3f);
	}

	@Override
	public void update(Player player, float deltaTime) {
		// TODO Auto-generated method stub
		super.update(player, deltaTime);
		if (isFinished()) {
			LightComponent lightComp = player.getComponent(LightComponent.class);
			lightComp.setRadius(lightComp.getRadius() / 3f);
		}
	}

	@Override
	public void reset() {
		super.reset();
		this.elapsedTimeToFinish = 10.f;
	}
}
