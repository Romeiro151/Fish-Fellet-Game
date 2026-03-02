package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import objects.Gravity.Weight; // Certifica-te que o import do Enum está correto

public class Trunk extends GameObject implements FixedObject{
    
    public Trunk(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "trunk";
    }

    @Override
    public int getLayer(){
        return 1;
    }

    // Método que verifica se o tronco é esmagado
    public boolean checkCrush(GameObject yObj) {

        // Verifica se o objeto tem gravidade
        if (yObj instanceof Gravity) {
            Gravity g = (Gravity) yObj;
            
            // Se for pesado, o tronco é esmagado e removido do jogo
            if (g.mObjectWeigth() == Weight.PESADO.getValue()) {
                getRoom().removeObject(this);
                ImageGUI.getInstance().removeImage(this); 
                return true;
            }
        }
        return false;
    }

    @Override
	public boolean mIsPassable(GameObject yMover){
		return false;
	}
}