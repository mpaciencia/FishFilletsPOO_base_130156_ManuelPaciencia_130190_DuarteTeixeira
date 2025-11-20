package objects;

import interfaces.Transposable;
import interfaces.Untransposable;
import pt.iscte.poo.game.Room;

public class HoledWall extends GameObject implements Untransposable, Transposable{

	public HoledWall(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "holedWall";
	}	

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean isTransposableBy(GameObject gameObject){
		//taça e peixe pequeno passam
		if(gameObject instanceof SmallFish || gameObject instanceof Cup)
			return true;
		return false;
	}
}
