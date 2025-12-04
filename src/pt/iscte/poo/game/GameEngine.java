package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import interfaces.*;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;

import objects.*;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;


public class GameEngine implements Observer {
	
	private Map<String,Room> rooms;
	private Room currentRoom;
	private int lastTickProcessed = 0;
	private boolean smallSelected = true;
	
	public GameEngine() {
		rooms = new HashMap<String,Room>();
		loadGame();
		currentRoom = rooms.get("room0.txt");

		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);

		SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
		BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
		updateGUI();	
	}

	private void loadGame() {
		File[] files = new File("./rooms").listFiles();
		for(File f : files) {
			rooms.put(f.getName(),Room.readRoom(f,this));
		}
	}

	@Override
	public void update(Observed source) {

		if(checkWin()){
			nextLevel();
			return;
		}

		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();

			if (Direction.isDirection(k)) {
				Direction dir = Direction.directionFor(k);

				if (smallSelected) {
					// Só move SE o peixe ainda estiver na lista de objetos da sala
					if (currentRoom.getObjects().contains(SmallFish.getInstance())) {
						SmallFish.getInstance().move(dir);
					}
				} else {
					// O mesmo para o peixe grande
					if (currentRoom.getObjects().contains(BigFish.getInstance())) {
						BigFish.getInstance().move(dir);
					}
				}
				for (GameObject obj : new ArrayList<>(currentRoom.getObjects())) {
        			if (obj instanceof Crab) {
            			((Crab) obj).moveRandom();
        			}	
    			}
			}
			else if (k == KeyEvent.VK_SPACE) {
				// Simular a troca
				boolean targetSelection = !smallSelected;

				// Verificar se o destino da troca é válido
				if (targetSelection) {
					// Quer mudar para o PEQUENO. Ele está na sala?
					if (currentRoom.getObjects().contains(SmallFish.getInstance())) {
						smallSelected = true;
						ImageGUI.getInstance().setStatusMessage("Peixe pikeno selecionado");
					}
				} else {
					// Quer mudar para o GRANDE. Ele está na sala?
					if (currentRoom.getObjects().contains(BigFish.getInstance())) {
						smallSelected = false;
						ImageGUI.getInstance().setStatusMessage("Peixe grande selecionado");
					}
				}
			}
			else if(k == KeyEvent.VK_R){
				ImageGUI.getInstance().showMessage("Nível reiniciado", "Tecla 'r' pressionada");
				restartGame();
			}
		}

		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {
			processTick();
		}
		ImageGUI.getInstance().update();
	}

	private void processTick() {
		
		//iteramos por uma copia dos objetos para evitar erros 
		for(GameObject obj : new ArrayList<>(currentRoom.getObjects())){
			if(obj instanceof GravityAffected){
				applyGravity((GravityAffected) obj);
			}
			if (obj instanceof Floatable) {
            	applyBuoyancy((Floatable) obj); // Método auxiliar novo
        	}
		}
		checkSmallFishCrush();
		checkBigFishCrush();
		checkTraps();
		checkTrunkCrush();
		checkCrabCollisions();
		lastTickProcessed++;
	}
	//metodo auxiliar para aplicar a gravidade
	public void applyGravity(GravityAffected objInterface) {
        GameObject obj = (GameObject) objInterface;
        
        // Se NÃO tem suporte -> cai
        if (!objInterface.isSupported()) {
            Point2D posBelow = obj.getPosition().plus(Direction.DOWN.asVector());
            obj.setPosition(posBelow);
            
            // Se for uma bomba, marcamos que está a cair
            if (obj instanceof Bomb) {
                ((Bomb) obj).setFalling(true);
            }
        } 
        // Se TEM suporte (bateu ou está parada)
        else {
            if (obj instanceof Bomb) {
                Bomb b = (Bomb) obj;
                
                // Se estava a cair E agora tem suporte -> boom
                if (b.isFalling()) {
                    
                    // Ver o que está por baixo
                    Point2D posBelow = b.getPosition().plus(Direction.DOWN.asVector());
                    GameObject support = currentRoom.getObjectAt(posBelow);
                    
                    // Explode se bater num objeto (excluindo peixes)
                    // isSupported já garante que não é Water.
                    // Só precisamos garantir que não é um GameCharacter (Peixe/Caranguejo)
                    if (!(support instanceof GameCharacter)) {
                        explode(b);
                    } else {
                        // Se caiu em cima de um peixe, não explode (o peixe suporta-a sem detonar)
                        // Mas o peixe pequeno morre pelo peso (tratado no checkSmallFishCrush)
                        b.setFalling(false); 
                    }
                }
            }
        }
    }

	public void applyBuoyancy(Floatable objInterface) {
        GameObject obj = (GameObject) objInterface;
        Point2D currentPos = obj.getPosition();
        Point2D posAbove = currentPos.plus(Direction.UP.asVector());
        Point2D posBelow = currentPos.plus(Direction.DOWN.asVector());

        GameObject objAbove = currentRoom.getObjectAt(posAbove);

        // Define se tem carga (algo que não seja Água nem Peixe)
        boolean hasLoad = (objAbove != null && objAbove instanceof GravityAffected);

        if (hasLoad) {
            // --- RAMO 1: TEM CARGA -> TENTA AFUNDAR ---
            GameObject objBelow = currentRoom.getObjectAt(posBelow);
            if (objBelow == null || objBelow instanceof Water) {
                obj.setPosition(posBelow);
            }
        } else {
            // --- RAMO 2: NÃO TEM CARGA -> TENTA SUBIR ---
            // Só sobe se o espaço acima for realmente água ou vazio (se for um peixe, fica quieta)
            boolean canMoveUp = (objAbove == null || objAbove instanceof Water);
            
            if (canMoveUp && posAbove.getY() >= 0) {
                obj.setPosition(posAbove);
            }
        }
    }
	// Método para tratar da explosão
    private void explode(Bomb bomb) {
        System.out.println("KABOOM!");
        Point2D bombPos = bomb.getPosition();
        
        // 1. Remover a própria bomba
        currentRoom.removeObject(bomb);
        
        // 2. Verificar as 4 direções adjacentes (Cima, Baixo, Esquerda, Direita)
        for (Direction dir : Direction.values()) {
            Point2D targetPos = bombPos.plus(dir.asVector());
            
            // Verificar se atingiu um peixe (Game Over)
            if (SmallFish.getInstance().getPosition().equals(targetPos) || 
                BigFish.getInstance().getPosition().equals(targetPos)) {
                ImageGUI.getInstance().showMessage("Game Over", "O peixe explodiu!");
                restartGame();
                return;
            }

            // Verificar se há objetos para destruir
            GameObject targetObj = currentRoom.getObjectAt(targetPos);
            if (targetObj != null && !(targetObj instanceof Water) && !(targetObj instanceof GameCharacter)) {
                // Remove paredes, pedras, etc.
                currentRoom.removeObject(targetObj);
            }
        }
    }

	public void checkTrunkCrush(){
		for(GameObject obj : new ArrayList<>(currentRoom.getObjects())){
			if(obj instanceof Trunk){
				Point2D posAbove = obj.getPosition().plus(Direction.UP.asVector());

				GameObject objAbove = currentRoom.getObjectAt(posAbove);

				if(objAbove instanceof interfaces.Heavy){
					currentRoom.removeObject(obj);
				}
			}
		}
	}

	public void checkSmallFishCrush(){
		Point2D currentPos = SmallFish.getInstance().getPosition();
		int stackWeight = 0;
		//ciclo para ver se o peixe pequeno
		while(true){
			//vamos buscar a posição acima
			currentPos = currentPos.plus(Direction.UP.asVector());
			GameObject objAbove = currentRoom.getObjectAt(currentPos);
			if(objAbove == null || !(objAbove instanceof GravityAffected))
				break;
			stackWeight++;
			//peixe pequeno nao suporta 1 pesado
			if(objAbove instanceof Heavy && !(objAbove instanceof Trap)){
				ImageGUI.getInstance().showMessage("Nivel reiniciado", "Peixe pequeno esmagado");
				restartGame();
				return;
			}
		}//peixe pequeno 
		if(stackWeight >= 2){
			ImageGUI.getInstance().showMessage("Nivel reiniciado", "Peixe pequeno esmagado");
			restartGame();
		}
	}

	public void checkBigFishCrush() {
		Point2D currentPos = BigFish.getInstance().getPosition();
		int heavyStackWeight = 0;
		// ciclo para ver o que o peixe grande suporta
		while (true) {
			// Vamos buscar a posição acima
			currentPos = currentPos.plus(Direction.UP.asVector());
			GameObject objAbove = currentRoom.getObjectAt(currentPos);
			//se nao tiver objetos em cima siga embora daqui
			if (objAbove == null || !(objAbove instanceof GravityAffected)) {
				break;
			}
			//Se for uma Armadilha, morre logo
			if (objAbove instanceof Trap) {
				ImageGUI.getInstance().showMessage("Game Over", "O Peixe Grande tocou na armadilha!");
				restartGame();
				return;
			}
			//incrementa o peso se tiver pesado em cima
			if (objAbove instanceof interfaces.Heavy) {
				heavyStackWeight++;
			}
		}
		if (heavyStackWeight >= 2) {
			ImageGUI.getInstance().showMessage("Nivel reiniciado", "Peixe Grandão esmagado");
			restartGame();
		}
	}
	private void checkCrabCollisions() {
		for (GameObject obj : new ArrayList<>(currentRoom.getObjects())) {
			if (obj instanceof Crab) {
				Point2D crabPos = obj.getPosition();

				// Colisão com Peixe Pequeno -> Game Over
				if (SmallFish.getInstance().getPosition().equals(crabPos)) {
					ImageGUI.getInstance().showMessage("Game Over", "O Peixe Pequeno foi apanhado pelo caranguejo!");
					restartGame();
					return;
				}

				// Colisão com Peixe Grande -> Caranguejo morre
				if (BigFish.getInstance().getPosition().equals(crabPos)) {
					currentRoom.removeObject(obj);
					continue; // Passa ao próximo objeto
				}

				// Colisão com Armadilha -> Caranguejo morre
				// procurar se há uma armadilha nesta posição
				for (GameObject t : currentRoom.getObjects()) {
					if (t instanceof Trap && t.getPosition().equals(crabPos)) {
						currentRoom.removeObject(obj);
						break;
					}
				}
			}
		}
	}
	//verifica se ambos os peixes sairam da sala, chamado no processTick
	public boolean checkWin() {
		return(!(currentRoom.getObjects().contains(BigFish.getInstance())) && !(currentRoom.getObjects().contains(SmallFish.getInstance())));
	}
	private void checkTraps() {
		// Verifica se o Peixe Grande está na mesma posição que alguma Armadilha
		Point2D bigFishPos = BigFish.getInstance().getPosition();
		GameObject obj = currentRoom.getObjectAt(bigFishPos);
		
		// Se o objeto na posição do peixe for uma Armadilha...
		if (obj instanceof Trap) {
			ImageGUI.getInstance().showMessage("Game Over", "O Peixe Grande caiu na armadilha!");
			restartGame();
		}
	}

	public void restartGame(){

		loadGame();
		String levelName = currentRoom.getName();
		currentRoom = rooms.get(levelName);

		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);

		SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
    	BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
		//resetar os atributos
		SmallFish.getInstance().resetState();
		BigFish.getInstance().resetState();
		
		updateGUI();
	}

	public void nextLevel(){
		//nivel atual
		String currentName = currentRoom.getName();
		//nome começa no quarto index (roomX) e tiramos o .txt
		String numberStr = currentName.substring(4,currentName.indexOf('.'));
		int currentLevelIndex = Integer.parseInt(numberStr);
		//proximo nivel é so somar um
		int nextLevelIndex = currentLevelIndex + 1;
		String nextRoomName = "room" + nextLevelIndex + ".txt";
		if(rooms.containsKey(nextRoomName)){
			ImageGUI.getInstance().showMessage("Nível completo", "Passaste ao nível" + nextLevelIndex);
			//nova sala
			currentRoom = rooms.get(nextRoomName);
			//os peixes mudaram de sala
			SmallFish.getInstance().setRoom(currentRoom);
			BigFish.getInstance().setRoom(currentRoom);
			//mete os peixes no novo mapa
			SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
        	BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
			//limpa estados antigos
			SmallFish.getInstance().resetState();
			BigFish.getInstance().resetState();

			updateGUI();
			System.out.println("Nivel" + nextLevelIndex + "iniciado!");
		} else {//nao existem mais niveis
			ImageGUI.getInstance().showMessage("vitoria", "completaste todos os niveis");
			ImageGUI.getInstance().dispose();
			System.exit(0);
		}
	}

	public void updateGUI() {
		if(currentRoom!=null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}
}
