package objects;

import interfaces.Untransposable;
import pt.iscte.poo.game.Room;

// Parede sólida: bloqueia totalmente a passagem.
// Não pode ser empurrada nem atravessada.
public class Wall extends GameObject implements Untransposable{

	public Wall(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "wall";
	}	

	@Override
	public int getLayer() {
		return 1;
	}

}
