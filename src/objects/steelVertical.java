package objects;

import interfaces.Untransposable;
import pt.iscte.poo.game.Room;

public class steelVertical extends GameObject implements Untransposable{
    public steelVertical(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "steelVertical";
	}

	@Override
	public int getLayer() {
		return 1;
	}

}
