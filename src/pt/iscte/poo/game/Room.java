package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import objects.Water;
import objects.Anchor;
import objects.BigFish;
import objects.Bomb;
import objects.Buoy;
import objects.Cup;
import objects.FixedObject;
import objects.GameCharacter;
import objects.GameObject;
import objects.HoledWall;
import objects.Key;
import objects.Portal;
import objects.SmallFish;
import objects.SteelHorizontal;
import objects.SteelVertical;
import objects.Stone;
import objects.Trap;
import objects.Trunk;
import objects.Wall;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.*;
import java.util.Scanner;

public class Room {
	
	private List<GameObject> objects;
	private String roomName;
	private GameEngine engine;
	private Point2D smallFishStartingPosition;
	private Point2D bigFishStartingPosition;
	
	public Room() {
		objects = new ArrayList<GameObject>();
	}

	private void setName(String name) {
		roomName = name;
	}
	
	public String getName() {
		return roomName;
	}
	
	private void setEngine(GameEngine engine) {
		this.engine = engine;
	}

	public GameEngine getEngine(){
		return engine;
	}

	public void addObject(GameObject obj) {
		objects.add(obj);
		engine.updateGUI();
	}
	
	public void removeObject(GameObject obj) {
		objects.remove(obj);
		engine.updateGUI();
	}
	
	public List<GameObject> getObjects() {
		return objects;
	}

	public void setSmallFishStartingPosition(Point2D heroStartingPosition) {
		this.smallFishStartingPosition = heroStartingPosition;
	}
	
	public Point2D getSmallFishStartingPosition() {
		return smallFishStartingPosition;
	}
	
	public void setBigFishStartingPosition(Point2D heroStartingPosition) {
		this.bigFishStartingPosition = heroStartingPosition;
	}
	
	public Point2D getBigFishStartingPosition() {
		return bigFishStartingPosition;
	}

	public static Room readRoom(File f, GameEngine engine) {
		Room r = new Room();
		r.setEngine(engine);

		r.setName(f.getName());

		try{

			int y = 0;
			Scanner s = new Scanner(f);

			while(s.hasNextLine()){
				String line = s.nextLine();

				if(line.length() != 10){
					ImageGUI.getInstance().showMessage("ERRO", "A linha " + (y+1)+ " do ficheiro " + f.getName() + "não tem exatamente 10 caracteres!");
					System.exit(0);
				}

				for(int x = 0; x < line.length(); x++){

					char letra = line.charAt(x);

					GameObject water = new Water(r);
					water.setPosition(new Point2D(x,y));
					r.addObject(water);

					switch (letra) {

						case 'W': {
							GameObject wall = new Wall(r);
							wall.setPosition(new Point2D(x, y));
							r.addObject(wall);
							break;
						}

						case 'B': {
							GameObject bf = BigFish.getInstance();
							bf.setPosition(new Point2D(x, y));
							r.addObject(bf);
							break;
						}

						case 'S': {
							GameObject sf = SmallFish.getInstance();
							sf.setPosition(new Point2D(x, y));
							r.addObject(sf);
							break;
						}

						case 'H': {
							GameObject sh = new SteelHorizontal(r);
							sh.setPosition(new Point2D(x, y));
							r.addObject(sh);
							break;
						}

						case 'V': {
							GameObject sv = new SteelVertical(r);
							sv.setPosition(new Point2D(x, y));
							r.addObject(sv);
							break;
						}

						case 'C': {
							GameObject cup = new Cup(r);
							cup.setPosition(new Point2D(x, y));
							r.addObject(cup);
							break;
						}

						case 'R': {
							GameObject stone = new Stone(r);
							stone.setPosition(new Point2D(x, y));
							r.addObject(stone);
							break;
						}

						case 'A': {
							GameObject anchor = new Anchor(r);
							anchor.setPosition(new Point2D(x, y));
							r.addObject(anchor);
							break;
						}

						case 'b': {
							GameObject bomb = new Bomb(r);
							bomb.setPosition(new Point2D(x, y));
							r.addObject(bomb);
							break;
						}

						case 'T': {
							GameObject trap = new Trap(r);
							trap.setPosition(new Point2D(x, y));
							r.addObject(trap);
							break;
						}

						case 'Y': {
							GameObject trunk = new Trunk(r);
							trunk.setPosition(new Point2D(x, y));
							r.addObject(trunk);
							break;
						}

						case 'X': {
							GameObject holedWall = new HoledWall(r);
							holedWall.setPosition(new Point2D(x, y));
							r.addObject(holedWall);
							break;
						}

						case 'F': {
							GameObject buoy = new Buoy(r);
							buoy.setPosition(new Point2D(x, y));
							r.addObject(buoy);
							break;
						}

						case 'P': {
							GameObject portal = new Portal(r);
							portal.setPosition(new Point2D(x,y));
							r.addObject(portal);
							break;
						}

						case 'a': {
							GameObject key = new Key(r);
							key.setPosition(new Point2D(x,y));
							r.addObject(key);
							break;
						}

						default:
							break;
					}
				}
				y++;
			}
			s.close();
			if(y != 10){
				ImageGUI.getInstance().showMessage("ERRO", "O ficheiro " + f.getName() + " não tem exatamente 10 linhas");
				System.exit(0);
			}

			int bigFish = 0;
			int smallFish = 0;

			for(GameObject go : r.getObjects()){
				if(go instanceof BigFish){
					bigFish++;
				}
				if(go instanceof SmallFish){
					smallFish++;
				}
			}
			if(bigFish != 1 || smallFish != 1){
				ImageGUI.getInstance().showMessage("ERRO", "O número de big fish é de small fish no ficheiro não é exatamente 1");
				System.exit(smallFish);
			}
		}
		catch(FileNotFoundException e){
			System.err.println("Não foi possível abrir o ficheiro dado");
		}
		
		return r;
	}

	// Método para obter uma lista com todos os objetos a baixo (útil quando temos o carangueijo e o peixe a tentarem estar dentro da HoledWall)
	public List<GameObject> getObjects(Point2D p) {
		List<GameObject> result = new ArrayList<>();
		for (GameObject o : objects) {
			if (p.equals(o.getPosition())) {
				result.add(o);
			}
		}
			return result;
		}

	// Método para obter apenas o Objeto a baixo
	public GameObject mGetObjectAt(Point2D yPos) {
		for (GameObject xObj : objects) {
			if (xObj.getPosition().equals(yPos) && xObj.getLayer() > 0) {
				return xObj;
			}
		}
		return null;
    }

	// Método para verificar se um objeto está bloqueado
	public boolean mIsBlocked(GameObject yMover, Point2D yDest) {

		List<GameObject> xObjs = getObjects(yDest);  // Lista de objetos na posição desejada para mover

		for(GameObject o : xObjs) {

			if(o instanceof FixedObject){   // Se algum Objeto de destino for Fixo
				if(!((FixedObject) o).mIsPassable(yMover)){  // Se não for passável
					return true;  // Devolve True
				}
			}

			if(o instanceof GameCharacter){  // Se estiver bloqueado por um peixe
				return true;  // Também devolve True
			}

			if(o instanceof Stone){
				return true;
			}
		}
		return false;  // Caso contrário não está bloqueado
	}

	// Função para verificar se algum objeto está suportado
	public boolean mIsSupported(Point2D yPoint, int yWeight){

		Point2D xBaixo = yPoint.plus(Direction.DOWN.asVector());  // Armazena a posição a baixo

		List<GameObject> xObjs = getObjects(xBaixo);  // Armezana a lista de objetos a baico

		for(GameObject xO : xObjs){
			if(xO instanceof FixedObject && !(xO instanceof Trunk)){ // Se o objeto estiver suportado por um objeto fixo exceto o Trunk (para partir o Trunk)
				return true;
			}
		} 
		
		return false;
	}

	// Procedimento para reiniciar o jogo
	public void mResetGame(){
		if(engine != null){
			engine.mChangeRoom(roomName);
		}
	}

	// Procedimento que serve para explodir a bomba
	public void explode(Point2D centro) {

		// Cria um array com as 5 coordenadas afetadas pela bomba.
		Point2D[] explosao = {
			centro,                                      // O centro (onde a bomba estava)
			centro.plus(Direction.UP.asVector()),        // Uma casa acima
			centro.plus(Direction.LEFT.asVector()),      // Uma casa à esquerda
			centro.plus(Direction.RIGHT.asVector()),     // Uma casa à direita
			centro.plus(Direction.DOWN.asVector())       // Uma casa abaixo
		};

		// Fase de Deteção: Identificar quem vai desaparecer
		List<GameObject> xRemove = new ArrayList<>();
		boolean xFishExploded = false;
		// GameObject explodedFish = null; // (Opcional: Variável útil apenas se quiseres saber QUAL peixe morreu especificamente)

		// Percorre cada uma das 5 posições da explosão
		for (Point2D xP : explosao) {
			
			// Obtém todos os objetos nessa coordenada (pode haver chão + peixe, por exemplo)
			List<GameObject> xObjs = getObjects(xP);
			
			for (GameObject xGO : xObjs) {
				// Proteção: A água nunca é destruída pela bomba (é o fundo do mapa)
				if (xGO instanceof Water) continue;

				// Marca o objeto para remoção
				xRemove.add(xGO);

				// Verifica se o objeto atingido é um dos Jogadores (Peixe)
				// Nota: Se usares 'instanceof GameCharacter' abrange ambos automaticamente
				if (xGO instanceof SmallFish || xGO instanceof BigFish) {
					xFishExploded = true;
					// explodedFish = go; 
				}
			}
		}

		// 3. Fase de Destruição: Apagar objetos do Jogo e do Ecrã
		// Itera sobre a lista de condenados. Usamos uma cópia ou a lista auxiliar para evitar erros de concorrência.
		for (GameObject go : new ArrayList<>(xRemove)) {
			removeObject(go);                       // Remove da lógica (lista 'objects' da Room)
			ImageGUI.getInstance().removeImage(go); // Remove da visualização (janela gráfica)
		}

		// Força a atualização visual imediata (para os objetos sumirem antes da mensagem)
		ImageGUI.getInstance().update();

		// 4. Fase de Consequência: Verificar Game Over
		if (xFishExploded) {
			// Mostra mensagem ao jogador
			ImageGUI.getInstance().showMessage("Explosão", "O peixe foi explodido!");
			
			// Penaliza o jogador nas estatísticas
			engine.mAddTentativas();
			
			// Reinicia o nível atual
			if (engine != null) {
				engine.mChangeRoom(roomName); // Recarrega o ficheiro da sala atual
			}
			return; // Sai da função para evitar lógica extra desnecessária
		}

		// Se nenhum peixe morreu, a função termina aqui.
		// Os blocos/inimigos foram destruídos, mas o jogo continua.
	}

	// Método para verificar se está dentro do mapa
	public boolean mIsWithinBounds(Point2D pos) {
    	return pos.getX() >= 0 && pos.getX() < 9
           && pos.getY() >= 0 && pos.getY() < 9;
	}

	public Point2D getPortalDest(Point2D original){
		for(GameObject go : objects){
			if(go instanceof Portal && !go.getPosition().equals(original)){
				return go.getPosition();
			}
		}
		return null;
	}
	
}