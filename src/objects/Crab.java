package objects;
import java.util.Random;
import interfaces.GravityAffected;
import interfaces.Small;
import interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

// Caranguejo: inimigo móvel que mata os peixes ao tocar.
// Ele comporta-se como um personagem (move-se), 
// sofre gravidade, é pequeno (passa buracos) e é transponível (para permitir colisão/morte quando os peixes lhe tocam).
public class Crab extends GameCharacter implements GravityAffected, Small, Transposable{
    public Crab(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "krab"; 
    }

    @Override
    public int getLayer() {
        return 2;
    }

    // Permite que peixes e o caranguejo ocupem a mesma casa (para detetar colisão)
    @Override
    public boolean isTransposableBy(GameObject gameObject) {
        return true;
    }

    @Override
    public boolean isSupported() {
        Point2D posBelow = getPosition().plus(Direction.DOWN.asVector());
        GameObject objBelow = getRoom().getObjectAt(posBelow);

        if(objBelow != null && !(objBelow instanceof Water) && !(objBelow instanceof Transposable))
            return true;
        return false;
    }

    // Lógica de movimento aleatório (apenas Esquerda/Direita)
    public void moveRandom() {
        Direction[] possibleDirs = {Direction.LEFT, Direction.RIGHT};
        int index = new Random().nextInt(possibleDirs.length);
        super.move(possibleDirs[index]);
    }
}