package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;
import java.util.List;

public class Bomb extends GameObject implements Pushable{
    
    public Bomb(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "bomb";
    }

    @Override
    public int getLayer(){
        return 1;
    }

    // Método para verificar por quem a bomba é movivél (ambos os peixes)
    @Override
    public boolean mIsPushableBy(GameObject yMover){
        return yMover instanceof SmallFish || yMover instanceof BigFish;
    }

    // Método auxiliar para verificar quem a bomba explode (tudo exceto Wall, peixes ou Water)
    private boolean mShouldExplode(GameObject yObject){
        if(yObject == null){
            return false;
        }

        return !(yObject instanceof NotExplodable);
    }

    @Override
    public boolean mPush(Vector2D yDir){

        Point2D xAtual = getPosition();
        Point2D xDestino = xAtual.plus(yDir);

        // Verifica se está bloquado, caso não esteja move-se para a posição desejada
        if(!getRoom().mIsBlocked(this, xDestino)){
            setPosition(xDestino);
            return true;
        }
        return false;
    }

    // Método verifica se um objeto está suportado, através da função já criada no Room
    @Override
    public boolean isSupported() {
        return getRoom().mIsSupported(getPosition(), mObjectWeigth());
    }

    // Método vai tratar da gravidade dos objetos
    @Override
    public boolean mGravity(){

        // Verifica se está suportado, caso sim não faz nada
        if(isSupported()){
            return false;
        }

        Vector2D xDown = Direction.DOWN.asVector();  
        Point2D xDest = getPosition().plus(xDown);  // A gravidade é a posição a baixo
        
        List<GameObject> xObjectDest = getRoom().getObjects(xDest);
        boolean xExplode = false;  // Variável para verificar se a bomba explodiu ou não

        // Percorre todos os objetos no destino e verifica se  a bomba deve explodir ao colidir com esses objetos
        for(GameObject xGO : xObjectDest){
            if(mShouldExplode(xGO)){
                xExplode = true;
                break;
            }
        }

        if(xExplode){
            getRoom().explode(getPosition());
            return false;
        }

        if(getRoom().mIsBlocked(this, xDest)){
            return true;
        }

        setPosition(xDest);
        return true;
    }

    @Override
    public int mObjectWeigth(){
        return Weight.LEVE.getValue();
    }
}
