package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import interfaces.*;

import java.awt.Image;
import java.awt.event.KeyEvent;
import objects.SmallFish;
import objects.BigFish;
import objects.GameObject;
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
		updateGUI();		
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
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

			if(Direction.isDirection(k)){
				Direction dir = Direction.directionFor(k);
			
				if(smallSelected){
					SmallFish.getInstance().move(dir);
					ImageGUI.getInstance().setStatusMessage("Peixe pikeno");}
				else{
					BigFish.getInstance().move(dir);
					ImageGUI.getInstance().setStatusMessage("Peixe grande");
				}
			}
			else if(k == KeyEvent.VK_SPACE){
				smallSelected = !smallSelected;
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
				Point2D posBelow = obj.getPosition().plus(Direction.DOWN.asVector());
				GravityAffected fallingObj = (GravityAffected) obj;
				//boolean peixePequenoEmBaixo = SmallFish.getInstance().getPosition().equals(posBelow);
				//não está suportado? cai
				if(!fallingObj.isSupported()){
					obj.setPosition(posBelow);
				}
			}
		}
		checkSmallFishCrush();
		checkBigFishCrush();
		lastTickProcessed++;
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
			if(objAbove instanceof Heavy){
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
			}//incrementa o peso se tiver pesado em cima
			if (objAbove instanceof interfaces.Heavy) {
				heavyStackWeight++;
			}
		}
		if (heavyStackWeight >= 2) {
			ImageGUI.getInstance().showMessage("Nivel reiniciado", "Peixe Grandão esmagado");
			restartGame();
		}
	}

	public boolean checkWin() {
		// verifica se esta fora dos limites peixe pikeno
		Point2D sPos = SmallFish.getInstance().getPosition();
		boolean smallFishOut = sPos.getX() < 0 || sPos.getX() >= 10 || sPos.getY() < 0 || sPos.getY() >= 10;
		
		// verifica se esta fora dos limites peixe grande
		Point2D bPos = BigFish.getInstance().getPosition();
		boolean bigFishOut = bPos.getX() < 0 || bPos.getX() >= 10 || bPos.getY() < 0 || bPos.getY() >= 10;

		// ganharam a champions
		return smallFishOut && bigFishOut;
	}

	public void restartGame(){

		loadGame();
		String levelName = currentRoom.getName();
		currentRoom = rooms.get(levelName);
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
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
