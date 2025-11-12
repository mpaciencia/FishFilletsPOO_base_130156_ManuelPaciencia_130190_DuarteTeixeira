package objects;

import interfaces.Intransposable;
import pt.iscte.poo.game.Room;

public class HoledWall extends GameObject implements Intransposable{

	public HoledWall(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "Holedwall";
	}	

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
    public void moveHandler() {
    }
}
