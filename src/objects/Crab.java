package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

// Implementa Gravity (para cair) e Pushable (para definirmos que NÃO é empurrável)
public class Crab extends GameObject implements Pushable, Enemy, SmallObject{

    public Crab(Room room, Point2D pos) {
        super(room);
        setPosition(pos);
    }

    @Override
    public String getName() {
        return "krab";
    }

    @Override
    public int getLayer() {
        return 2; // Mesmo layer dos peixes para haver colisão
    }

    //Método serve para obter obter o peso 
    @Override
    public int mObjectWeigth() {
        return Gravity.Weight.LEVE.getValue(); // É leve (1 ponto)
    }

    // Nunca pode ser empurrado
    @Override
    public boolean mIsPushableBy(GameObject yMover) {
        return false;
    }

    //Nunca vai ser emourrado
    @Override
    public boolean mPush(Vector2D yDir) {
        return false;
    }

    // Afunda se não estiver suportado
    @Override
    public boolean isSupported() {
        return getRoom().mIsSupported(getPosition(), mObjectWeigth());
    }

    // Função da gravidade exatamente igual
    @Override
    public boolean mGravity() {
        // Se já estiver suportado, não faz nada.
        if (isSupported()) {
            return false;
        }

        Point2D xDown = getPosition().plus(Direction.DOWN.asVector());
        GameObject xDest = getRoom().mGetObjectAt(xDown);

        // Verifica o objeto de destino
        if (xDest != null) {
            
            // Se for uma HoledWall, o caranguejo cai através dela (pois é SmallObject)
            if (xDest instanceof HoledWall  || xDest instanceof SmallFish) {
                setPosition(xDown);
                return true;
            }

            return false; 
        }

        // Se o caminho estiver livre (null), cai.
        setPosition(xDown);
        return true;
    }

   // Procedimento de mover o carangueijo
    @Override
    public void mMove() {

        // Gravidade (Prioridade Máxima)
        if (mGravity()){
            return;
        }

        // Escolher Direção e Calcular Destino
        Vector2D xDir = (Math.random() < 0.5) ? Direction.LEFT.asVector() : Direction.RIGHT.asVector();
        Point2D xNextPos = getPosition().plus(xDir);

        // Limites do Mapa
        if (!getRoom().mIsWithinBounds(xNextPos)){
            return;
        }
        // Verificar o que está no destino
        GameObject xTarget = getRoom().mGetObjectAt(xNextPos);

        // Caminho Livre
        if (xTarget == null) {
            setPosition(xNextPos);
            return;
        }

        // Combate (explicação nos comentários do método interaction)
        if (xTarget instanceof GameCharacter) {
            setPosition(xNextPos);
            boolean crabDied = this.mInteraction((GameCharacter) xTarget);
            
            // Se o Crab NÃO morreu (ou seja, matou o peixe), ele ocupa a posição.
            if (!crabDied) {
                setPosition(xNextPos);
            }
            return;
        }

        // Tratar da trap (Crab morre)
        if (xTarget instanceof Trap) {
            this.kill();
            return;
        }

        // Entra na HoledWall
        if (xTarget instanceof HoledWall) {
            setPosition(xNextPos);
            return;
        }
    }
    
    // Método que trata de interagir com os GameCharacter
    public boolean mInteraction(GameCharacter yFish){

        // Se o peixe grande for o peixe da interação
        if(yFish instanceof BigFish){
            this.kill();  // Mata o Crab
            return true;
        }

        // Se o peixe pequeno for o peixe da interação
        if(yFish instanceof SmallFish){
            yFish.mKill();  // Mata o peixe pequeno
            ImageGUI.getInstance().showMessage("GAME OVER", "O Crab comeu o peixe pequeno");
            return false;
        }
        return false;
    }

    // Método auxiliar para remover o caranguejo do jogo
    private void kill() {
        getRoom().removeObject(this);
        ImageGUI.getInstance().removeImage(this);
        ImageGUI.getInstance().update();
    }
}