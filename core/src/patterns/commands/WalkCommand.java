package patterns.commands;

import ecs.entities.Player;

public class WalkCommand extends Command {
	public enum Directions {
		UP, RIGHT, DOWN, LEFT
	}
	
	private Directions direction;
	private Player player;

	public WalkCommand(Player player, Directions direction, boolean singlePress) {
		super(singlePress);
		this.player = player;
		this.direction = direction;
	}
	
	@Override
	public void execute(float deltaTime) {
		player.walk(direction);
	}
}
