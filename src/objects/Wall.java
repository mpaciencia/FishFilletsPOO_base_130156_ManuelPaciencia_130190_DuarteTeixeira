package objects;

import interfaces.Intransposable;
import pt.iscte.poo.game.Room;

public class Wall extends GameObject implements Intransposable{

	public Wall(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "wall";
	}	

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void moveHandler(){}

}
