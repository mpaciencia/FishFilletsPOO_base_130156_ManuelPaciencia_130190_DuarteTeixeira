package objects;

import interfaces.Untransposable;
import pt.iscte.poo.game.Room;

// Azulejo de aço vertical: bloqueia totalmente a passagem.
// Variante do SteelHorizontal com sprite diferente.
public class SteelVertical extends GameObject implements Untransposable{
    public SteelVertical(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "steelVertical";
	}

	@Override
	public int getLayer() {
		return 1;
	}

}
