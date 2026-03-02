package objects;

import pt.iscte.poo.game.Room;

public class SteelHorizontal extends GameObject implements FixedObject{

	public SteelHorizontal(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "steelHorizontal";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean mIsPassable(GameObject yMover){
		return false;
	}
}
