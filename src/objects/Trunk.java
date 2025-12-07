package objects;

import interfaces.Breakable;
import interfaces.Untransposable;
import pt.iscte.poo.game.Room;

// Tronco: bloqueia a passagem.
// Pode ser quebrável (Breakable) sob certas condições ou ações do jogador.
public class Trunk extends GameObject implements Untransposable, Breakable{
    public Trunk(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "trunk";
    }

    @Override
    public int getLayer(){
        return 1;
    }
}
