package objects;
import interfaces.Big;
import interfaces.Heavy;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;

public class Anchor extends GameObject implements Pushable, Heavy{
    public Anchor(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "anchor";
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
