package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Buoy extends GameObject implements Pushable {

	public Buoy(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "buoy";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public int mObjectWeigth() {
		return Gravity.Weight.LEVE.getValue();
	}

	@Override
	public boolean mIsPushableBy(GameObject mover) {
		return true; 
	}

	@Override
    public boolean mPush(Vector2D yDir) {
        
		Point2D xAtual = getPosition();
        Point2D xDestino = xAtual.plus(yDir);

        // Verifica paredes e limites do mapa
        if (getRoom().mIsBlocked(this, xDestino)) {
            return false;
        }

        // Verifica se já existe um objeto nessa posição
        GameObject xObj = getRoom().mGetObjectAt(xDestino);
        
        // Se existir um objeto, a boia bate e não mexe.
        if (xObj != null) {
            return false; 
        }

        // Se estiver livre, move-se
        setPosition(xDestino);
        return true;
    }

	@Override
	public boolean isSupported() {

		Point2D xPosAbove = getPosition().plus(Direction.UP.asVector());
		GameObject xObjAbove = getRoom().mGetObjectAt(xPosAbove);
		
		if (xObjAbove instanceof Pushable) {
			return getRoom().mIsSupported(getPosition(), mObjectWeigth());
		}
		return getRoom().mIsBlocked(this, xPosAbove);
	}

	@Override
	public boolean mGravity() {

		Direction[] position = {Direction.DOWN, Direction.LEFT, Direction.RIGHT};
		int random = (int) (Math.random() * position.length);
		Direction dir = position[random];
		Point2D nextPos = getPosition().plus(dir.asVector());


		Point2D below = getPosition().plus(Direction.DOWN.asVector());
		GameObject xObjBelow = getRoom().mGetObjectAt(below);

		// Tem objeto móvel em cima -> afunda (Gravidade normal para baixo)
		if (nextPos instanceof Pushable) {
			if (!getRoom().mIsSupported(getPosition(), mObjectWeigth())) {
				Point2D down = getPosition().plus(Direction.DOWN.asVector());
				setPosition(down);
				return true;
			}
			return false;
		}

		//Livre em cima -> flutua (Gravidade invertida para cima)
		if (!getRoom().mIsBlocked(this, nextPos)) {
			setPosition(nextPos);
			return true;
		}
		
		return false;
	}
}