package objects;


import interfaces.Big;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

// Peixe grande: pode empurrar objetos pesados (Stone, Anchor) marcados com interface Big.
// Singleton para haver apenas uma instância no jogo.
// Alterna sprite (esquerda/direita) consoante a direção do movimento efetivo.
public class BigFish extends GameCharacter implements Big{

	private static BigFish bf = new BigFish(null);
	private String currentImage = "bigFishLeft"; // imagem inicial
	
	private BigFish(Room room) {
		super(room);
	}

	// Retorna a única instância do BigFish (padrão Singleton)
	public static BigFish getInstance() {
		return bf;
	}
	
	@Override
	public String getName() {
		return currentImage;
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void move(Direction dir) {
		Point2D before = getPosition();
		super.move(dir);
		Point2D after = getPosition();
		// Só muda sprite se efetivamente se moveu;
		if (!after.equals(before)) {
			if (dir == Direction.LEFT) {
				currentImage = "bigFishLeft";
			} else if (dir == Direction.RIGHT) {
				currentImage = "bigFishRight";
			}
		}
	}
}
