package objects;

import interfaces.Heavy;
import interfaces.Transposable;
import pt.iscte.poo.game.Room;

public class Trap extends GameObject implements Heavy{
    public Trap(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "trap";
    }

    @Override
    public int getLayer(){
        return 0;
    }


}
