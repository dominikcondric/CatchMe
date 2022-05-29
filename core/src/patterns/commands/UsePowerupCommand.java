package patterns.commands;

import ecs.entities.Player;

public class UsePowerupCommand extends Command {
	private Player player;
		
	public UsePowerupCommand(Player player, boolean singlePress) {
		super(singlePress);
		this.player = player;
	}

	@Override
	public void execute(float deltaTime) {
		player.usePowerUp();
	}
}
