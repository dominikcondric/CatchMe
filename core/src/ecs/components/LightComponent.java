package ecs.components;

public class LightComponent implements Component {
	float positionX, positionY, radius;
	float lightIntensity = 1.f;

	public LightComponent(float positionX, float positionY, float radius, float lightIntensity) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.radius = radius;
		this.lightIntensity = lightIntensity;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		if (radius > 0f)
			this.radius = radius;
	}

	public float getX() {
		return positionX;
	}

	public void setX(float positionX) {
		this.positionX = positionX;
	}

	public float getY() {
		return positionY;
	}

	public void setY(float positionY) {
		this.positionY = positionY;
	}

	public float getLightIntensity() {
		return lightIntensity;
	}

	public void setLightIntensity(float lightIntensity) {
		this.lightIntensity = lightIntensity;
	}
}
