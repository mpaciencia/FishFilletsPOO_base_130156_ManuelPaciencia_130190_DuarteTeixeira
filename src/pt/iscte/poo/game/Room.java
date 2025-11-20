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

		// Inicialmente preenche toda a grelha 10x10 com água
		// Isto garante que cada posição tem um objecto base (Water).
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				GameObject water = new Water(r);
				water.setPosition(new Point2D(i, j));
				r.getObjects().add(water);
			}
		}

		// Lê o ficheiro do nível linha a linha e converte caracteres em objectos
		// Cada linha representa uma linha y do mapa; cada caractere uma coluna x.
		try (Scanner scanner = new Scanner(f)) {
			int y = 0; // Coordenada y atual (linha)
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				// Para cada carácter na linha, criamos o objecto correspondente
				for (int x = 0; x < line.length(); x++) {
					char c = line.charAt(x);
					Point2D position = new Point2D(x, y);
					GameObject obj = null; // Vai conter o objecto criado para esta célula

					switch (c) {
						case 'W':
							// Parede sólida
							obj = new Wall(r);
							break;
						case 'B':
							// Posição inicial do peixe grande (apenas guardamos a posição)
							obj = BigFish.getInstance();
							r.setBigFishStartingPosition(position);
							break;
						case 'S':
							// Posição inicial do peixe pequeno (apenas guardamos a posição)
							obj = SmallFish.getInstance();
							r.setSmallFishStartingPosition(position);
							break;
						case 'H':
							// Azulejo de aço horizontal
							obj = new SteelHorizontal(r);
							break;
						case 'X':
							// Parede com buraco
							obj = new HoledWall(r);
							break;
						case 'C':
							obj = new Cup(r);
							break;
						case 'R':
							obj = new Stone(r);
						case 'A':
							obj = new Anchor(r);
						case 'b':
							obj = new Bomb(r);
						case 'T':
							obj = new Trap(r);
						case 'Y':
							obj = new Trunk(r);
						default:
							// Qualquer outro carácter é ignorado (mantém-se Water existente)
							break;
					}

					// Se foi criado um objecto, define a sua posição e adiciona à lista
					if (obj != null) {
						obj.setPosition(position);
						r.getObjects().add(obj);
					}
				}
				y++; // Passa para a próxima linha (y+1)
			}

		} catch (FileNotFoundException e) {
			// Em caso de erro a abrir o ficheiro, informa no stderr e imprime stacktrace
			System.err.println("ERRO: Ficheiro do nível não encontrado: " + f.getName());
			e.printStackTrace();
		}

		// Retorna a Room construída (com água e objectos sobrepostos onde aplicável)
		return r;
	}
	
}