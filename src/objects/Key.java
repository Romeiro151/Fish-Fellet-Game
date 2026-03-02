package objects;

import pt.iscte.poo.game.Room;

public class Key extends GameObject{
    
    public Key(Room r){
        super(r);
    }

    @Override
    public int getLayer(){
        return 1;
    }

    @Override
    public String getName(){
        return "key";
    }
}
