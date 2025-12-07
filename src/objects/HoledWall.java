package objects;

import interfaces.Small;
import interfaces.Transposable;
import interfaces.Untransposable;
import pt.iscte.poo.game.Room;

// Parede com buraco: bloqueia em geral mas pode ser atravessável por certos objetos.
// Implementa Transposable para definir regras (ex.: só SmallFish passa).
public class HoledWall extends GameObject implements Untransposable, Transposable{

	public HoledWall(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "holedWall";
	}	

	@Override
	public int getLayer() {
		return 1;
	}

	// Apenas objetos Small (SmallFish) conseguem atravessar o buraco
	@Override
	public boolean isTransposableBy(GameObject gameObject){
		//taça e peixe pequeno passam
		if(gameObject instanceof Small)
			return true;
		return false;
	}
}
