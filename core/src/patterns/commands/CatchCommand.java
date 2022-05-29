package patterns.commands;

import ecs.entities.Player;

public class CatchCommand extends Command {
	private Player player;
	
	public CatchCommand(Player player, boolean singlePress) {
		super(singlePress);
		this.player = player;
	}

	@Override
	public void execute(float deltaTime) {
		player.performCatch();
	}
}
