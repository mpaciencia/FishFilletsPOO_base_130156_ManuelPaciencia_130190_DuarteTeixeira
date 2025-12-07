package objects;

import pt.iscte.poo.game.Room;

// Água: objeto de fundo (layer 0) presente em todas as células da grelha.
// Não bloqueia passagem — os peixes nadam livremente sobre água.
public class Water extends GameObject{

	public Water(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "water";
	}

	@Override
	public int getLayer() {
		return 0;
	}

}
