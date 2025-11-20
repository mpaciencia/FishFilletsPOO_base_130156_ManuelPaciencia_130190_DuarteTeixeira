package objects;

import interfaces.Untransposable;
import pt.iscte.poo.game.Room;

public class HoledWall extends GameObject implements Untransposable{

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
}
