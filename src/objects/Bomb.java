package objects;

import interfaces.Explosive;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;

public class Bomb extends GameObject implements Pushable, Explosive{
    public Bomb(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "bomb";
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
