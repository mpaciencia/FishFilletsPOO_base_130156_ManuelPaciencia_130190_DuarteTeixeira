package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import objects.*;
import pt.iscte.poo.utils.Point2D;

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

		// Cria uma nova Room e associa o engine e o nome do ficheiro
		Room r = new Room();
		r.setEngine(engine);
		r.setName(f.getName());

		// Inicialmente preenche tudo com água
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				GameObject water = new Water(r);
				water.setPosition(new Point2D(i, j));
				r.getObjects().add(water);
			}
		}


		try (Scanner scanner = new Scanner(f)) {
			int y = 0; 
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				//nao deixa ler ficheiros que nao sejam 10x10
				if (line.length() > 10) {
					throw new IllegalArgumentException("Erro no mapa " + f.getName() + ": Linha " + y + " demasiado longa (" + line.length() + " chars). Max: 10.");
				}
				if (y >= 10) {
					throw new IllegalArgumentException("Erro no mapa " + f.getName() + ": O ficheiro tem demasiadas linhas (Max: 10).");
				}
				for (int x = 0; x < line.length(); x++) {
					char c = line.charAt(x);
					Point2D position = new Point2D(x, y);
					GameObject obj = null; 

					switch (c) {
						case 'W':
							obj = new Wall(r);
							break;
						case 'B':
							obj = BigFish.getInstance();
							r.setBigFishStartingPosition(position);
							break;
						case 'S':
							obj = SmallFish.getInstance();
							r.setSmallFishStartingPosition(position);
							break;
						case 'H':
							obj = new SteelHorizontal(r);
							break;
						case 'X':
							obj = new HoledWall(r);
							break;
						case 'C':
							obj = new Cup(r);
							break;
						case 'R':
							obj = new Stone(r);
							break;
						case 'A':
							obj = new Anchor(r);
							break;
						case 'b':
							obj = new Bomb(r);
							break;
						case 'T':
							obj = new Trap(r);
							break;
						case 'Y':
							obj = new Trunk(r);
							break;
						case 'V':
							obj = new SteelVertical(r);
							break;
						default:
							// qualquer outro caracter é ignorado
							break;
					}

					// adicionar objeto á lista
					if (obj != null) {
						obj.setPosition(position);
						r.getObjects().add(obj);
					}
				}
				y++;
			}

		} catch (FileNotFoundException e) {
			System.err.println("ERRO: Ficheiro do nível não encontrado: " + f.getName());
			e.printStackTrace();
		}

		return r;
	}
	
}