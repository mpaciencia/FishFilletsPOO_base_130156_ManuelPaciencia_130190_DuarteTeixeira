package objects;

import interfaces.Big;
import interfaces.Lightweight;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;

public class Stone extends GameObject implements Pushable, Lightweight{
    public Stone(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "rock";
    }

    @Override
    public int getLayer(){
        return 1;
    }
    @Override
    public boolean isPushableBy(GameObject gameObject){
        if(gameObject instanceof Big)
            return true;
        return false;
    }
}
