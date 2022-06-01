package com.gdx.game.powerups;

import ecs.components.PhysicsComponent;
import ecs.entities.Player;

public class SpeedBootsPowerUp extends PowerUp {

	public SpeedBootsPowerUp() {
		super(2, 8, 10f);
	}

	@Override
	protected void takeEffect(Player player) {
		player.getComponent(PhysicsComponent.class).setMoveMultiplier(10f);
	}

	@Override
	public void update(Player player, float deltaTime) {
		super.update(player, deltaTime);
		if (isFinished()) {
			player.getComponent(PhysicsComponent.class).setMoveMultiplier(5f);
		}
	}

	@Override
	public void reset() {
		super.reset();
		elapsedTimeToFinish = 10.f;
	}
}
