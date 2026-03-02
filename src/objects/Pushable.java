package objects;

import pt.iscte.poo.utils.Vector2D;

public interface Pushable extends Gravity{
    
    //Determina se certo objeto é "empurrável" por um Character que se está a mover (neste caso small ou bigfish)
    boolean mIsPushableBy(GameObject yMove);

    //Método que vai tratar de movimentar o objeto
    boolean mPush(Vector2D yDir);
}
