package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;;

public class Trap extends GameObject implements Pushable{
    
    public Trap(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "trap";
    }

    @Override
    public int getLayer(){
        return 1;
    }

    // Se o peixe grande tenta empurrar a Trap, ele morre
    @Override
    public boolean mIsPushableBy(GameObject yMover){
        if(yMover instanceof BigFish){
            BigFish.getInstance().mKill();
            ImageGUI.getInstance().showMessage("GAME OVER", "O Peixe Grande morreu na Trap");
            return false;
        }
        // Peixe pequeno não consegue empurrar, porque Trap é pesada
        if(yMover instanceof SmallFish){
            return false;
        }
        return true;
    }

    @Override
    public boolean mPush(Vector2D dir){

        Point2D xAtual = getPosition();
        Point2D xDest = xAtual.plus(dir);

        if(!getRoom().mIsBlocked(this, xDest)){
            GameObject xObj = getRoom().mGetObjectAt(xDest);
            if(xObj != null && xObj.getLayer() > 0){
                return false;
            }
            
            setPosition(xDest);
            return true;
        }
        return false;
    }

    @Override
    public boolean isSupported() {
        return getRoom().mIsSupported(getPosition(), mObjectWeigth());
    }

    @Override
    public boolean mGravity() {
        
        if (isSupported()) {
            return false;
        }
        
        Vector2D xGravity = Direction.DOWN.asVector(); 
        Point2D xDest = getPosition().plus(xGravity);

        GameObject xObj = getRoom().mGetObjectAt(xDest);
        
        // Tenta Esmagar o Tronco. Se for Trunk, remove-o e continua a queda.
        if (xObj instanceof Trunk) {
            if(((Trunk) xObj).checkCrush(this)){
                xObj = null;
            }
        }

        // Se for outro objeto sólido (que não Trunk), não cai.
        else if (xObj != null) {
            return false;
        }
        
        // Verifica se a posição de destino está bloqueada por obstáculos inquebráveis
        if (getRoom().mIsBlocked(this, xDest)) {
            return false;
        }

        //Move o objeto para baixo
        setPosition(xDest);
        
        return true; 
    }

    @Override
    public int mObjectWeigth(){
        return Weight.PESADO.getValue();
    }
}