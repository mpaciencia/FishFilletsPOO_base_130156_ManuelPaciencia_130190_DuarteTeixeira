package objects;

import interfaces.GravityAffected;
import interfaces.Heavy;
import interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

// Armadilha: objeto pesado com gravidade que pode matar os peixes.
// É transponível (para permitir colisão e detecção de morte).
public class Trap extends GameObject implements Heavy, GravityAffected, Transposable{
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
    
    // Permite passagem dos peixes para detetar colisão (morte)
    @Override
    public boolean isTransposableBy(GameObject gameObject) {
        // Deixa passar toda a gente.
        // A morte do BigFish será tratada no GameEngine quando colidirem.
        return true; 
    }

    @Override
    public boolean isSupported() {
        Point2D posBelow = this.getPosition().plus(Direction.DOWN.asVector());
        GameObject objBelow = getRoom().getObjectAt(posBelow);
        // px pequeno nao suporta armadilha
        if (objBelow instanceof SmallFish) {
            return false;
        }
        
        if (objBelow != null && !(objBelow instanceof Water)) {
            return true;
        }
        return false;
    }

}
