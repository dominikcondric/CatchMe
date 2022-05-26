package ecs.components;

import com.badlogic.gdx.math.Vector2;

public class LightComponent implements Component {
	private Vector2 position;
	private float radius;
	private float lightIntensity = 1.f;

	public LightComponent(float positionX, float positionY, float radius, float lightIntensity) {
		position = new Vector2(positionX, positionY);
		this.radius = radius;
		this.lightIntensity = lightIntensity;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		if (radius >= 0f)
			this.radius = radius;
	}

	public void setPosition(float positionX, float positionY) {
		this.position.x = positionX;
		this.position.y = positionY;
	}
	
	public Vector2 getPosition() {
		return new Vector2(position);
	}
	
	public void setPosition(Vector2 position) {
		setPosition(position.x, position.y);
	}

	public float getLightIntensity() {
		return lightIntensity;
	}

	public void setLightIntensity(float lightIntensity) {
		this.lightIntensity = lightIntensity;
	}
}
