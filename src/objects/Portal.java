package objects;

import pt.iscte.poo.game.*;

public class Portal extends GameObject{
    
    public Portal(Room r){
        super(r);
    }

    @Override
    public int getLayer(){
        return 1;
    }

    @Override
    public String getName(){
        return "portal";
    }

    
}
