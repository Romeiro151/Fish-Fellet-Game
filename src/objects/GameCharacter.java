package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;
import pt.iscte.poo.gui.ImageGUI;
import java.util.List;
import java.util.ArrayList;
import pt.iscte.poo.game.GameEngine;

public abstract class GameCharacter extends GameObject implements NotExplodable{

    private boolean xIsFacingRight;
    private boolean xLeftRoom = false;
    private GameEngine engine;
	
    public GameCharacter(Room room) {
        super(room);
    }

    public void setEngine(GameEngine engine){
        this.engine = engine;
    }

    public GameEngine getEngine(){
        return engine;
    }

    public boolean mHasLeftRoom(){
        return xLeftRoom;
    }

    public void mSetRoom(boolean yLeftRoom){
        this.xLeftRoom = yLeftRoom;
    }

    public boolean mGetFacingRight(){
        return xIsFacingRight;
    }

    // Método para tratar da morte de um GameCharacter
    public void mKill() {
        getRoom().getEngine().mAddTentativas();   // Incrementa as tentativas
        ImageGUI.getInstance().removeImage(this);  // Remove a imagem do GameCharacter que morreu
        getRoom().removeObject(this);    // Remove o GameCharacter que morreu
        ImageGUI.getInstance().update(); // Atualiza a GUI
        mSetRoom(true);   // Considera-se que sai do Room para evitar bugs de quando o jogo reinicia o peixe estar lá sem imagem
        getRoom().mResetGame();    //Reseta o jogo
    }

    //Método auxiliar para empurrar vários objetos
    private boolean mPushHorizontally(Point2D yPos, Vector2D yDir) {
        Point2D xCurrent = yPos;
        ArrayList<Pushable> xChain = new ArrayList<>();  // Lista de objetos a serem empurrados

        // Enquanto ouver objetos para empurrar
        while (true) {
            GameObject xObject = getRoom().mGetObjectAt(xCurrent);  // Variável para o Objeto que está no posição atual

            // Se for um objeto empurrável
            if (xObject instanceof Pushable) {
                Pushable xPushableObj = (Pushable) xObject;  // Transforma o objeto em Pushable
                xChain.add(xPushableObj);  // Adiciona o objeto à lista
                xCurrent = xCurrent.plus(yDir);  // A posição atual passa a ser na posição seguinte (como se tivesse andado para frente)
            } else {
                break;  // Se não for empurrável paramos o loop
            }
        }

        //Verificação para quando o fim não é espaço vazio (não dá para empurrar)
        if (getRoom().mGetObjectAt(xCurrent) != null)
            return false;

        //Loop para ir empurrando todos os objetos, começando no mais distante (último da lista)
        for (int i = xChain.size() - 1; i >= 0; i--) {
            if(!xChain.get(i).mPush(yDir)){
                return false;
            }
        }

        return true;  // Devolve true se tudo correr bem
    }
	
    public boolean move(Vector2D yDir) {
        
        //Variável local para retornar o valor final (se se moveu ou não) (se o objeto foi movido com sucesso retorna positivo, caso contrário retorna negativo)
        boolean xMoved = false;

        //Verificação serve para alterar a imagem do peixe (se o x da direção dada como argumento for positivo está virado para a direita, caso contrário para a esquerda)
        if (yDir.getX() != 0){
            xIsFacingRight = yDir.getX() > 0;
        }
        
        Point2D xAtual = getPosition();
        Point2D xDestination = xAtual.plus(yDir);

        // Verificação para quando o peixe estiver bloqueado por um FixedObject ou por um peixe
        if (getRoom().mIsBlocked(this, xDestination)) {
            return false; //Devolve false, pois ninguém se move
        }

        //Verificação para o caso da posição destino seja uma posição de saída do mapa
        if(!getRoom().mIsWithinBounds(xDestination)){

            //Caso saia, a variável xLeftRoom torna-se verdadeira (ou seja objeto saiu do mapa)
            mSetRoom(true);

            //Verificação para atualizar o jogo caso algum peixe saia do jogo
            if(mHasLeftRoom()){

                ImageGUI.getInstance().removeImage(this); //Remove a imagem do peixe que saiu
                getRoom().removeObject(this);  //Remove o peixe que saiu
                ImageGUI.getInstance().update();  //Atualiza a GUI
            }
            return false;
        }

        // Objeto no destino
        List<GameObject> xObjects = getRoom().getObjects(xDestination);

        GameObject xEnemyFound = null;
        GameObject xObject = null;


        // Necessário para verificar se estão 2 objetos dentro da HoledWall e para fazerem o que é suposto
        for(GameObject xObj : xObjects){

            if(xObj instanceof Enemy){
                xEnemyFound = xObj;
            }
            if(xObject == null || xObj.getLayer() > xObject.getLayer()){
                xObject = xObj;
            }
        }

        if(xEnemyFound != null){
            xObject = xEnemyFound;
        }

        if(xObject instanceof Portal){
            Point2D PortalDest = getRoom().getPortalDest(xDestination);

            if(PortalDest != null){
                setPosition(PortalDest);
                xMoved = true;
            }
            return false;  
        }

        // Verifica interação com o Crab
        if (xObject instanceof Enemy) {
            
            // Variável para verificar se o Enemy morreu
            boolean xCrabDead = ((Crab) xObject).mInteraction(this);

            // Se o Crab morre
            if(xCrabDead){
                setPosition(xDestination);  // Ocupamos a posição dele
            }
            else{
                return false;
            }
        }

        // Verificação para empurrar outros objetos
        if (xObject instanceof Pushable) {

            Pushable xPushableObj = (Pushable) xObject;  // Transforma xObject em Pushable
            boolean xPushed = false;   // Variável local para devolver caso o o objeto tenho sido empurrado(devolve true se sim, caso contrário devolve false)

            // Verificação 1 - Se for BigFish na horizontal:
            if (this instanceof BigFish && yDir.getY() == 0) {
                // Tenta empurrar (ou morre na Trap)
                if (xPushableObj.mIsPushableBy(this)) {
                    xPushed = mPushHorizontally(xDestination, yDir);  // Altera o valor do xPushed caso seja possível empurrar uma fila do objetos ou não
                }
            }
            // Verificação 2 - Se for SmallFish ou movimento vertical:
            else {
                // Se for o SmallFish a tentar empurrar uma Boia para baixo (Y > 0), impedimos.
                boolean xIsSmallFishPushingBuoyDown = (this instanceof SmallFish) && (xPushableObj instanceof Buoy) && (yDir.getY() > 0);

                // Peixe empurra tudo para baixo exceto a Boia (caso seja um objeto que possa empurrar para baixo)
                if (!xIsSmallFishPushingBuoyDown && xPushableObj.mIsPushableBy(this)) {
                    xPushed = xPushableObj.mPush(yDir);   // Altera o valor do xPushed caso tenho conseguido empurrar ou não
                }
            }

            // Caso tenho sido empurrado
            if (xPushed) {
                setPosition(xDestination);  // Atualiza a posição
                getRoom().getEngine().subOxigenioEmpurrar();
                xMoved = true;   // Altera o valor da variável xMoved para True, uma vez que já se moveu
            }

            // Caso se trate de um trap  a "ser empurrada" por um peixe pequeno
            else if(xObject instanceof Trap && this instanceof SmallFish){
                setPosition(xDestination);  // O peixe pequeno ocupa a posição da Trap
                xMoved = true;
            }

        } 
        // Nada empurrável → move só o peixe
        else {
            setPosition(xDestination);
            getRoom().getEngine().subOxigenioMover();
            xMoved = true;
        }

        return xMoved;
    }

    // Método para reiniciar a variável xLeftRoom (necessário quando reiniciamos o jogo)
    public void mResetLeftRoom(){
        this.xLeftRoom = false;
    }

    @Override
    public int getLayer() {
        return 2;
    }
}