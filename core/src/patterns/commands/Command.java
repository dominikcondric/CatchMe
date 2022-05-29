package patterns.commands;

public abstract class Command {
	private final boolean singlePress;
	
	public Command(boolean singlePress) {
		this.singlePress = singlePress;
	}
	
	public boolean isSinglePress() {
		return singlePress;
	}
	
	public abstract void execute(float deltaTime);
}
