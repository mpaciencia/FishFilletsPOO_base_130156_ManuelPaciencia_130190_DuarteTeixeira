package objects;

import interfaces.Big;
import interfaces.GravityAffected;
import interfaces.Heavy;
import interfaces.Lightweight;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

// Pedra: bloqueia passagem mas pode ser empurrada pelo BigFish (interface Big).
// Se o destino da pedra estiver livre, move-se; caso contrário bloqueia o peixe.
// Tem gravidade (GravityAffected) e pode spawnar caranguejo quando cai.
public class Stone extends GameObject implements Pushable, Heavy, GravityAffected{
     // Pedra bloqueia passagem, mas pode ser empurrada pelo BigFish (via marcação Big).
    // Regras de empurrar: destino da pedra tem de estar livre de Untransposable.
    private boolean hasSpawnedCrab = false;
    public Stone(Room room){
        super(room);
    }
    public boolean hasSpawnedCrab(){
        return hasSpawnedCrab;
    }
    public void setSpawnedCrab(boolean status) {
    this.hasSpawnedCrab = status;
    }

    @Override
    public String getName(){
        return "stone";
    }

    @Override
    public int getLayer(){
        return 1;
    }
    
    // Apenas objetos marcados como Big (BigFish) podem empurrar pedras
    @Override
    public boolean isPushableBy(GameObject gameObject){
        // Só permite empurrar por personagens/objetos marcados como Big.
        if(gameObject instanceof Big)
            return true;
        return false;
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
