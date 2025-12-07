package objects;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;
import pt.iscte.poo.game.Room;
import interfaces.Big;
import interfaces.GravityAffected;
import interfaces.Heavy;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;

// Âncora: objeto pesado que bloqueia passagem.
// Pode ser empurrado pelo BigFish (devido à marcação Big).
// Sujeito a gravidade (GravityAffected).
public class Anchor extends GameObject implements Pushable, Heavy, GravityAffected{
    // Âncora é pesada e bloqueia. Opcionalmente, só pode ser empurrada pelo BigFish.
    // Pode ter regras extra (ex.: não se move se o peixe já se mexeu nessa jogada).
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
    
    // Só o BigFish (Big) pode empurrar âncoras pesadas
    @Override
    public boolean isPushableBy(GameObject gameObject){
        // Só permite empurrar por personagens/objetos marcados como Big.
        return gameObject instanceof Big;
    }

    @Override
    public boolean isSupported(){
        Point2D posBelow = this.getPosition().plus(Direction.DOWN.asVector());
        GameObject objBelow = getRoom().getObjectAt(posBelow);
        if(objBelow != null && !(objBelow instanceof Water)){
            return true;
        }
        return false;  
    }
}
