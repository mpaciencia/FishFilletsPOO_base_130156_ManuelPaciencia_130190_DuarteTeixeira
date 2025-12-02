package objects;

import interfaces.Lightweight;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;

public class Buoy extends GameObject implements Pushable, Lightweight{
    public Buoy(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "buoy";
    }

    @Override
    public int getLayer(){
        return 1;
    }

    @Override
    public boolean isPushableBy(GameObject gameObject){
        if(gameObject instanceof GameCharacter)
            return true;
        return false;
    }
}
