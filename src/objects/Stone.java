package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Stone extends GameObject implements Pushable {

    private boolean xHasSpawnedCrab = false;

    public Stone(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "stone";
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public int mObjectWeigth() {
        return Gravity.Weight.PESADO.getValue();
    }

    @Override
    public boolean mIsPushableBy(GameObject yMover) {
        return yMover instanceof BigFish;
    }

    @Override
    public boolean mPush(Vector2D yDir) {
        Point2D xAtual = getPosition();
        Point2D xDest = xAtual.plus(yDir);

        // Verificar bloqueios da sala (Paredes, Limites)
        if (getRoom().mIsBlocked(this, xDest)) {
            return false;
        }

        //  Verificar se já existe um objeto no destino
        GameObject xObj = getRoom().mGetObjectAt(xDest);
        
        // Se houver objeto no destino
        if (xObj != null && xObj.getLayer() != 0) {
            // Se o objeto for um Tronco, partimo-lo e permitimos o movimento
            if (xObj instanceof Trunk) {
                getRoom().removeObject(xObj);
            } 
            // Se for qualquer outro objeto sólido, a pedra não mexe
            else {
                return false; 
            }
        }

        // Movimentar a pedra
        setPosition(xDest);

        // Lógica de Spawn do Caranguejo
        if (!xHasSpawnedCrab && yDir.getY() == 0) {
            
            Point2D xCrabSpawn = xDest.plus(Direction.UP.asVector());
            
            // Só spawna se a posição acima estiver livre
            if(getRoom().mGetObjectAt(xCrabSpawn) == null) {
                getRoom().addObject(new Crab(getRoom(), xCrabSpawn));
                xHasSpawnedCrab = true;
            }
        }
        return true;
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
        
        // Tenta esmagar o Tronco. Se for Trunk, remove-o e continua a queda.
        if (xObj instanceof Trunk) {
            if(((Trunk) xObj).checkCrush(this)){
                xObj = null;
            }
        }
        
        // Se for outro objeto sólido (que não Trunk), não cai.
        else if (xObj != null) {
            return false;
        }
        
        // Verifica se a posição de destino está bloqueada por obstáculos
        if (getRoom().mIsBlocked(this, xDest)) {
            return false;
        }

        // Move o objeto para baixo
        setPosition(xDest);
        
        return true; 
    }
}