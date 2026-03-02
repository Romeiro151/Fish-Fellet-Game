package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.awt.event.KeyEvent;
import objects.SmallFish;
import objects.BigFish;
import objects.GameCharacter;
import objects.GameObject;
import objects.Gravity;
import objects.Enemy;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class GameEngine implements Observer {
    
    private Map<String,Room> rooms;
    private Room currentRoom;
    private int lastTickProcessed = 0;
    private boolean xBigFishTurn = true;
    private String xCurrentRoomNameString = "room0.txt";
    private int xBigFishMoves = 0;
    private int xSmallFishMoves = 0;
    private List<Stats> xStats = new ArrayList<>();
    private Time xTempo = new Time();
    private int xTickOffset = 0;
    private int  xTentativas = 1;
    private String xPlayer;
    private int xTotalFishMoves = 0;
    private int xTotalBigFishMoves = 0;
    private int xTotalSmallFishMoves = 0;
    private int xOxigenio = 50;
    
    public GameEngine() {
        rooms = new HashMap<String,Room>();
        loadGame();
        currentRoom = rooms.get("room0.txt");

        while(true){
            
            String xInput = ImageGUI.getInstance().showInputDialog("NOME", "Introduz o teu nome, por favor!");

            if(xInput == null){
                System.exit(0);
            }
            else if(xInput.trim().isEmpty()){
                ImageGUI.getInstance().showInputDialog("NOME", "Introduz o teu nome, por favor");
            }
            else{
                this.xPlayer = xInput;
                break;
            }
        }
       
        this.xTickOffset = ImageGUI.getInstance().getTicks();
        updateGUI();        
        SmallFish.getInstance().setRoom(currentRoom);
        BigFish.getInstance().setRoom(currentRoom);
    }

    public int getOxigenio(){
        return xOxigenio;
    }

    public void subOxigenioMover(){
        xOxigenio--;
    }

    public void subOxigenioEmpurrar(){
        xOxigenio-=5;
    }

    //Getter do nome do Player
    public String mGetPlayerName(){
        return xPlayer;
    }

    // Atualiza o nome do Player
    public void mSetPlayerName(String yPlayer){

        //Validação para verificar se o nome foi inserido corretamente
        if(yPlayer != null && ! yPlayer.isBlank()){  
            this.xPlayer = yPlayer.trim();  // Atualiza o nome do Player
        }else{
            throw new IllegalArgumentException("Nome inválido, digite novamente!");   //Lança a exceção
        }
    }

    // Getter das tentativas
    public int mGetTentativas(){
        return xTentativas;
    }

    //Incrementa tentativas
    public void mAddTentativas(){
        xTentativas++;
    }

    // Procedimento que vai tratar de carregar o jogo
    private void loadGame() {
        File[] files = new File("./rooms").listFiles();  // Vamos guardar num array todos os ficheiros do diretório rooms
        for(File f : files) {
            rooms.put(f.getName(),Room.readRoom(f,this));   // Para cada ficheiro inicializa lê e carrega o jogo
        }
    }

    // Procedimento mais importante do trabalho. É o motor do jogo
    @Override
    public void update(Observed source) {

        boolean xMoved = false;  // Variável responsável para verificar se um objeto se move

        // Verificação que alguma tecla foi pressionada
        if (ImageGUI.getInstance().wasKeyPressed()) {

            int k = ImageGUI.getInstance().keyPressed();  // Armazena numa variável local k essa tecla pressionada

            //Verifica se a tecla pressionada foi o espaço, caso os peixes ainda estiverem no Room, pois se não tiverem não faz sentido trocar de turno
            if(k == KeyEvent.VK_SPACE && !BigFish.getInstance().mHasLeftRoom() && !SmallFish.getInstance().mHasLeftRoom()){
                xBigFishTurn = !xBigFishTurn;  // Troca a vez de jogar
            }

            // Verifica se foi uma seta pressionada
            else if(Direction.isDirection(k)){
                //Verifica se estamos na vez de jogar do peixe grande
                if(xBigFishTurn){
                    //Verifica se o peixe grande se consegue movimentar
                    if(BigFish.getInstance().move(Direction.directionFor(k).asVector())){
                        xBigFishMoves++;  // Incrementa o número de movimentos do big fish
                        xMoved = true;  // Altera a variável para True
                    }
                }else{
                    //Verifica se o peixe pequeno se consegue movimentar
                    if(SmallFish.getInstance().move(Direction.directionFor(k).asVector())){
                        xSmallFishMoves++;  // Incrementa o número de movimentos do small fish
                        xMoved = true;  // Altera a variável para True
                    }
                }
            }
        }

        // Se se mexeu com sucesso
        if(xMoved){
            // Primeiro movemos os inimigos (Caranguejos)
            mMoveEnemies();
            // Depois aplicamos a gravidade (para acentar tudo)
            mApplyGravityToAllObjects();
        }

        // Variável que guarda o valor caso o peixe tenha saido ou não do mapa, dependendo do turno atual
        boolean xFishEscaped = xBigFishTurn ? BigFish.getInstance().mHasLeftRoom() : SmallFish.getInstance().mHasLeftRoom();

        // Verifica se o peixe saiu e o nível ainda não está terminado (apenas um dos peixes saiu, o outro ainda está no mapa)
        if(xFishEscaped && !mIsLevelOver()){
            xBigFishTurn = !xBigFishTurn;  // Troca o turno
        }

        //Verifica se o nível terminou
        if(mIsLevelOver()){

            // Variável para guardar o nome do próximo mapa (vamos começar a tratar da troca de mapa)
            String xNextRoom = mGetNextRoomName();

            // Guardamos os movimentos totais dos dois peixes para depois apresentar-mos na ScoreBoard
            xTotalBigFishMoves += xBigFishMoves;
            xTotalSmallFishMoves += xSmallFishMoves;

            // Verifica se há próximo nível
            if(xNextRoom != null){

                mChangeRoom(xNextRoom);  // Troca de Room
                xBigFishMoves = 0;  // Reinicia os movimentos (PROVAVELMENTE VOU TIRAR ISTO, NÃO FAZ MUITO SENTIDO)
                xSmallFishMoves = 0; 
            }
            //se já não h+a mais níveis
            else{
                xTotalFishMoves = xTotalBigFishMoves + xTotalSmallFishMoves;  
                xStats.add(new Stats(xPlayer, xTotalFishMoves, xTempo.mGetTotalSeconds(), xTentativas));  // Cria um novo Stats com as informações deste jogo
                ScoreBoard.saveScore(xStats);  // Guarda as informações do Stats da linha a cima
                ScoreBoard.showScoreBoard();  // Mostra o ScoreBoard
                System.exit(0);  // Termina o jogo
            }
        }
        
        
        int xTicks = ImageGUI.getInstance().getTicks();  // Obter o tempo desde que a janela abriu
        int xRealTime = xTicks - xTickOffset;  // Retira o OffSet (tempo que estamos no parte de escrever o nome)

        // Enquanto o último tick não foi processado
        while (lastTickProcessed < xRealTime) {
            processTick();  // Excecuta a lógica do jogo
            xTempo.mTick();  // Avança o relógio
        }

        //Atualiza a GUI
        ImageGUI.getInstance().update();

        // Verifica se é a vez do peixe grande
        if (xBigFishTurn) {
            ImageGUI.getInstance().setStatusMessage("BigFish turn - Moves: " + xBigFishMoves + " | Tempo: " + xTempo + " | Tentativas: " + xTentativas + " | Oxigénio disponível: " + getOxigenio());  // Atualiza a mensagem do título
        } else {
            ImageGUI.getInstance().setStatusMessage("SmallFish turn - Moves: " + xSmallFishMoves + " | Tempo: " + xTempo + " | Tentativas: " + xTentativas + " | Oxigénio disponível: " + getOxigenio());  // Atualiza a mensagem do título
        }

        if(runOutOfAir()){
            ImageGUI.getInstance().showMessage("GAME OVER", "RAN OUT OF AIR");
            currentRoom.mResetGame();
            xOxigenio = 50;
        }
    }

    // Procedimento para trocar o mapa
    public void mChangeRoom(String yNewFileName) {
        
        this.xCurrentRoomNameString = yNewFileName;  // Atualiza o nome do Room atual para o Room que recebemos

        File xFile = new File("./rooms/" + yNewFileName);  // Cria um ficheiro novo com o nome do novo ficheiro na diretória rooms
        Room xNewRoom = null;  // Inicializa o novo Room

        // Verifica se o ficheiro existe
        if (xFile.exists()) {
            xNewRoom = Room.readRoom(xFile, this);  // lê o novo ficheiro
        } else {
            throw new IllegalArgumentException("Erro: Ficheiro da sala não encontrado em ./rooms/" + yNewFileName);  // Se o ficheiro não existe lança uma exceção
        }

        // Verica se o ficheiro é nulo
        if (xNewRoom == null)
            return;  // Não faz nada

        this.currentRoom = xNewRoom;  // Altera o Room atual para o novo Room

        SmallFish.getInstance().setRoom(this.currentRoom); 
        BigFish.getInstance().setRoom(this.currentRoom);

        SmallFish.getInstance().mResetLeftRoom();
        BigFish.getInstance().mResetLeftRoom();

        // Reinicia as stats quando o jogo começou
        this.xSmallFishMoves = 0;
        this.xBigFishMoves = 0;
        xBigFishTurn = true;
        updateGUI();
    }

    private void processTick() {    
        
        mApplyGravityToAllObjects();   // Gravidade contínua (para coisas que caiam independentemente do turno)
        checkFishCrushed();  // Verifica se algum peixe foi esmagado
        lastTickProcessed++;
    }

    // Procedimento move o enemigo (Carangueijo apenas)
    private void mMoveEnemies() {
        // Criamos uma cópia da lista para evitar erros se o caranguejo for removido durante o loop
        List<GameObject> xObjects = new ArrayList<>(currentRoom.getObjects());
        
        // Percorre todos os objetos da lista, caso algum deles for o carangueijo, move-o
        for (GameObject xO : xObjects) {
            if (xO instanceof Enemy) {
                ((Enemy) xO).mMove(); 
            }
        }
    }

    // Procedimento trata da aplicar a gravidade a todos os objetos
    private void mApplyGravityToAllObjects(){
        
        List<GameObject> xObjects = new ArrayList<>(currentRoom.getObjects());
        // Percorre todos os objetos de um room e aplicamos gravidade, caso eles tenham gravidade
        for(GameObject o : xObjects){
            if(o instanceof Gravity){
                ((Gravity) o).mGravity();
            }
        }
    }
    
    // Método auxiliar para verificar se um peixe saiu do Room
    private boolean mHasFishLeft(GameCharacter yFish){
        return yFish.mHasLeftRoom();
    }


    // Método auxiliar para verificar se um nível terminou (ambos os peixes sairam do Room)
    private boolean mIsLevelOver(){
        return mHasFishLeft(BigFish.getInstance()) && mHasFishLeft(SmallFish.getInstance());
    }

    // Método auxiliar para obter o nome do mapa do nível seguinte
    private String mGetNextRoomName(){
        try{
            String xNum = xCurrentRoomNameString.replaceAll("[^0-9]","");
            int xCurrent = Integer.parseInt(xNum);
            int xNext = xCurrent + 1;

            String nextRoom = "room" + xNext + ".txt";
            if(rooms.containsKey(nextRoom)){
                return nextRoom;
            }else{
                return null;
            }
        }catch(NumberFormatException e){
            System.err.println("Erro");
            return null;
        }
    }
    
    //Atualiza a GUI
    public void updateGUI() {
        if(currentRoom!=null) {
            ImageGUI.getInstance().clearImages();
            ImageGUI.getInstance().addImages(currentRoom.getObjects());
        }
    }
    
    // Método auxiliar para obter o peso total em cima de um peixe
    private int mWeightAbove(GameCharacter yFish) {
        
        int xTotalWeight = 0; // Variável que vai armazenar o peso total

        Point2D xPos = yFish.getPosition();  // Variável armazena a posição atual do peixe
        Point2D xPosUp = xPos.plus(Direction.UP.asVector());  // Variável armazena a posição a cima do peixe
        
        // Verifica se o Room está nulo
        if (currentRoom == null){
            return 0;  // Devolve 0 caso for verdade
        }

        GameObject xObj = currentRoom.mGetObjectAt(xPosUp);  // Variável obtem o Objeto da posição a cima

        // Enquanto os objetos acima forem objetos com gravidade 
        while (xObj instanceof Gravity) {
            xTotalWeight += ((Gravity) xObj).mObjectWeigth();  // Adiciona o peso do objeto a cima ao peso total
            xPosUp = xPosUp.plus(Direction.UP.asVector());  // Altera a posição a cima para a outra posição a cima
            xObj = currentRoom.mGetObjectAt(xPosUp);  // Altera o Objeto da posição a cima para a outra posição a cima
        }

        return xTotalWeight;  // Devolve o peso total
    }

    // Procedimento verifica se um peixe está a ser esmagado
    private void checkFishCrushed() {

        // SmallFish morre se tiver qualquer peso >= PESADO (2)
        int xWeightSmall = mWeightAbove(SmallFish.getInstance());
        if (xWeightSmall >= Gravity.Weight.PESADO.getValue()) {
            SmallFish.getInstance().mKill();
            ImageGUI.getInstance().showMessage("GAME OVER", "Small fish died. Restart");
            return;
        }

        // BigFish morre se tiver peso >= 2 * PESADO (4)
        int xWeightBig = mWeightAbove(BigFish.getInstance());
        if (xWeightBig >= 2 * Gravity.Weight.PESADO.getValue()) {
            BigFish.getInstance().mKill();
            ImageGUI.getInstance().showMessage("GAME OVER", "Big fish died. Restart");
            return;
        }
    }

    private boolean runOutOfAir(){
        return xOxigenio <= 0;
    }
}