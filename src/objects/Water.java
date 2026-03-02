package objects;

import pt.iscte.poo.game.Room;

public class Water extends GameObject implements NotExplodable{

	public Water(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "water";
	}

	@Override
	public int getLayer() {
		return 0;
	}

}
