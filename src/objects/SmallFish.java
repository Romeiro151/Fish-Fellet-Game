package objects;

import pt.iscte.poo.game.Room;

public class SmallFish extends GameCharacter implements SmallObject{

	private static SmallFish sf = new SmallFish(null);
	
	private SmallFish(Room room) {
		super(room);
	}

	public static SmallFish getInstance() {
		return sf;
	}
	
	@Override
	public String getName() {
		return mGetFacingRight() ? "smallFishRight" : "smallFishLeft";
	}

	@Override
	public int getLayer() {
		return 1;
	}
}
