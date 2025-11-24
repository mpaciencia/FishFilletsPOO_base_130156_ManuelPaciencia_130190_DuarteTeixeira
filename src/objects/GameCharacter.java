package objects;

import java.util.List;

import interfaces.Transposable;
import interfaces.Untransposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject implements Untransposable{
	
	public GameCharacter(Room room) {
		super(room);
	}
	
	public void move(Direction dir) {
		Vector2D v = dir.asVector();
		boolean canMove = true;
		List<GameObject> todosOsObjetos = getRoom().getObjects();
		Point2D destination = getPosition().plus(v);
		
		for(GameObject obj : todosOsObjetos){
			if(obj.getPosition().equals(destination)){
				if(obj instanceof Untransposable){
					//dentro dos objetos untransposable, vemos quais podem ser passados pelos pikenos
					if(obj instanceof Transposable){
						Transposable t = (Transposable) obj;
						if(t.isTransposableBy(this)){
							continue;
						}
					}
					canMove = false;
					break;
				}
			}
		}
		if(canMove)
		setPosition(destination);	
	}

	@Override
	public int getLayer() {
		return 2;
	}
}