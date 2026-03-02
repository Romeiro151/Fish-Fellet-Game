package objects;

import java.util.List;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Cup extends GameObject implements Pushable, SmallObject{
    
    public Cup(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "cup";
    }

    @Override
    public int getLayer(){
        return 1;
    }

    @Override
    public boolean mIsPushableBy(GameObject yMover){
        return yMover instanceof SmallFish || yMover instanceof BigFish;
    }

    @Override
    public boolean mPush(Vector2D dir){
        Point2D xAtual = getPosition();
        Point2D xDest = xAtual.plus(dir);

        // Já trata de passar pela HoledWall 
        if (getRoom().mIsBlocked(this, xDest)) {
            return false;
        }
        
        // Se não está bloqueado, verificamos apenas se não há outros objetos sólidos
        GameObject xObj = getRoom().mGetObjectAt(xDest);
        if(xObj != null && xObj.getLayer() > 0 && !(xObj instanceof HoledWall)){
            return false;
        }

        setPosition(xDest);
        return true;
    }

    @Override
    public boolean isSupported() {
        Vector2D xGravity = Direction.DOWN.asVector();
        Point2D xDest = getPosition().plus(xGravity);

        // Verifica se dest está dentro dos limites do mapa
        if (!getRoom().mIsWithinBounds(xDest)) {
            return false;
        }

        List<GameObject> xObjects = getRoom().getObjects(xDest);

        boolean xHasSupport = false;

        for(GameObject xO : xObjects){
            if(xO instanceof GameCharacter || xO instanceof Wall || xO instanceof Pushable || xO instanceof Trunk){
                xHasSupport = true;
            }
        }  
        
        return xHasSupport;
    }

    @Override
    public boolean mGravity(){

        // Se tiver suporte não faz nada (HoledWall não é suporte só para o Cup)
        if(isSupported()){
            return false;
        }

        Vector2D xGravity = Direction.DOWN.asVector();
        Point2D xDest = getPosition().plus(xGravity);
        GameObject xObj = getRoom().mGetObjectAt(xDest);

        // Cai para dentro da HoledWall
        if(xObj instanceof HoledWall){
            setPosition(xDest);
            return true;
        }

        if(getRoom().mIsBlocked(this, xDest)){
            return false;
        }

        setPosition(xDest);
        return true;
    }
    
    @Override
    public int mObjectWeigth(){
        return Weight.LEVE.getValue();
    }
}
