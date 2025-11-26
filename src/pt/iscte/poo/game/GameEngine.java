package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import interfaces.*;

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
		ImageGUI gui = ImageGUI.getInstance();

		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();

			if(Direction.isDirection(k)){
				Direction dir = Direction.directionFor(k);
			
				if(smallSelected){
					SmallFish.getInstance().move(dir);
					gui.setStatusMessage("Peixe pikeno");}
				else{
					BigFish.getInstance().move(dir);
					gui.setStatusMessage("Peixe grande");
				}
			}
			else if(k == KeyEvent.VK_SPACE){
				smallSelected = !smallSelected;
			}
			else if(k == KeyEvent.VK_R){
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
				boolean peixePequenoEmBaixo = SmallFish.getInstance().getPosition().equals(posBelow);
				//não está suportado? cai
				if(!fallingObj.isSupported() || peixePequenoEmBaixo && obj instanceof Heavy){
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
				System.out.println("Peixe morreu esmagado por gorda");
				restartGame();
				return;
			}
		}//peixe pequeno 
		if(stackWeight >= 2){
			System.out.println("RIP demasiados leves");
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
			if (objAbove instanceof interfaces.Heavy) {
				heavyStackWeight++;
			}
		}
		if (heavyStackWeight >= 2) {
			System.out.println("Peixe Grande esmagado por " + heavyStackWeight + " gordas");
			restartGame();
		}
	}

	public void restartGame(){

		System.out.println("nivel reiniciado");
		loadGame();
		String levelName = currentRoom.getName();
		currentRoom = rooms.get(levelName);
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
//		SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
//      BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
		updateGUI();
	}

	public void updateGUI() {
		if(currentRoom!=null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}
}
