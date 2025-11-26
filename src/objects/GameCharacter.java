package objects;

import java.util.List;
import interfaces.*;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject implements Untransposable{
    
    private boolean hasAlreadyMoved;

    public GameCharacter(Room room) {
        super(room);
        this.hasAlreadyMoved = false;
    }
    
    public void move(Direction dir) {
		Vector2D v = dir.asVector();
	    Point2D destination = getPosition().plus(v);
	    
	    boolean canMove = true; 
	    
	    List<GameObject> todosOsObjetos = getRoom().getObjects();
	    
	    for (GameObject obj : todosOsObjetos) {
	        if (obj.getPosition().equals(destination)) {
	            
	            if (obj instanceof Pushable) {
	                canMove = false; 
	                
	                if (((Pushable) obj).isPushableBy(this)) {
	                    Point2D pushDestination = destination.plus(v);
	                    GameObject target = getRoom().getObjectAt(pushDestination);
	                    GameObject beingPushed = getRoom().getObjectAt(destination);
	                    if (!(target instanceof Untransposable) || target instanceof Transposable)  {
	                    		if(beingPushed instanceof Anchor && (hasAlreadyMoved == false) && v.getY() == 0) {
	    	                    		obj.setPosition(pushDestination);
	    	       	                    canMove = true; 
	    	       	                    hasAlreadyMoved = true;	
	                    		}
	                    		
	                    	if(!(target instanceof Pushable) && !(beingPushed instanceof Anchor)) {
	                        obj.setPosition(pushDestination);
	                        canMove = true; 
	                    	}
	                    }
	                }
	                break;
	            }
	            if (obj instanceof Untransposable) {
	                if (obj instanceof Transposable) {
	                    if (!((Transposable) obj).isTransposableBy(this)) {
	                         canMove = false;
	                         break; 
	                    }
	                } 
	                else {
	                    canMove = false;
	                    break; 
	                }
	            }
	        }
	    }
	    
	    if (canMove) {
	        setPosition(destination);
	    }	
	}

    @Override
    public int getLayer() {
        return 2;
    }
}