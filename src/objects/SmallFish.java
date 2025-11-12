package objects;

import interfaces.Intransposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class SmallFish extends GameCharacter implements Intransposable{

	private static SmallFish sf = new SmallFish(null);
	private String currentImage = "smallFishLeft.png"; // adicionado
	
	private SmallFish(Room room) {
		super(room);
	}

	public static SmallFish getInstance() {
		return sf;
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
		// Só muda sprite se efetivamente se moveu; ADICIONADO
		if (!after.equals(before)) {
			if (dir == Direction.LEFT) {
				currentImage = "smallFishLeft.png";
			} else if (dir == Direction.RIGHT) {
				currentImage = "smallFishRight.png";
			}
		}
	}
	@Override
    public void moveHandler() {
    }
}
