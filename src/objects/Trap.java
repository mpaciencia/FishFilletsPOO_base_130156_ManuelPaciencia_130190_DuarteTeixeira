package objects;

import interfaces.Heavy;
import interfaces.Transposable;
import pt.iscte.poo.game.Room;

public class Trap extends GameObject implements Heavy, Transposable{
    public Trap(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "trap";
    }

    @Override
    public int getLayer(){
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
