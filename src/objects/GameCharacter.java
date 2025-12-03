package objects;

import java.util.ArrayList;
import java.util.List;
import interfaces.*;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject implements Untransposable{
    
    boolean hasAlreadyMoved;

    public GameCharacter(Room room) {
        super(room);
        this.hasAlreadyMoved = false;
    }
    
	public void move(Direction dir) {
        Vector2D v = dir.asVector();
        Point2D destination = getPosition().plus(v);

        // 1. Verificar limites do mapa
        if (destination.getX() < 0 || destination.getX() >= 10 || destination.getY() < 0 || destination.getY() >= 10) {
            getRoom().removeObject(this);
            return;
        }

        List<GameObject> objects = getRoom().getObjects();
        boolean blocked = false;
        
        // Verificar o que está na posição de destino
        for (GameObject obj : objects) {
            if (obj.getPosition().equals(destination)) {
                
                // --- CASO 1: Objeto é de Empurrar (Pushable) ---
                if (obj instanceof Pushable) {
                    if (((Pushable) obj).isPushableBy(this)) {
                        
                        // Tenta empurrar
                        if (!handlePushChain(v, destination)) {
                            blocked = true; 
                        } else {
                            // --- SUCESSO NO EMPURRÃO: TENTAR SPAWN DO CARANGUEJO ---
                            if (obj instanceof Stone && v.getY() == 0) {
                                Stone stone = (Stone) obj;
                                if (!stone.hasSpawnedCrab()) {
                                    // A pedra moveu-se para 'destination + v'
                                    Point2D stoneNewPos = destination.plus(v);
                                    Point2D spawnPos = stoneNewPos.plus(Direction.UP.asVector());
                                    
                                    GameObject objAbove = getRoom().getObjectAt(spawnPos);
                                    // Só nasce se o espaço acima estiver livre (null ou Water)
                                    if (objAbove == null || objAbove instanceof Water) {
                                        Crab babyCrab = new Crab(getRoom());
                                        babyCrab.setPosition(spawnPos);
                                        getRoom().addObject(babyCrab);
                                        stone.setSpawnedCrab(true);
                                    }
                                }
                            }
                            // -------------------------------------------------------
                        }
                    } else {
                        blocked = true;
                    }
                    break;
                }

                // --- CASO 2: Objeto é Parede (Untransposable) ---
                if (obj instanceof Untransposable) {
                    if (obj instanceof Transposable) {
                         if (!((Transposable) obj).isTransposableBy(this)) {
                            blocked = true;
                        }
                    } else {
                        blocked = true;
                    }
                }
            }
        }

        if (!blocked) {
            setPosition(destination);
        }
    }

	public void resetState() {
		this.hasAlreadyMoved = false;
	}

	private boolean handlePushChain(Vector2D v, Point2D startPos) {
	    List<GameObject> toPush = new ArrayList<>();
	    Point2D currentPos = startPos;

	    while (true) {
	        GameObject objAhead = getTopObjectAt(currentPos);

	        // 1. Se encontrámos Espaço Vazio (null)
	        if (objAhead == null) {
	            break; // Caminho livre
	        }

	        // 2. Se encontrámos uma Parede com Buraco (Transposable)
	        if (objAhead instanceof Transposable && objAhead instanceof Untransposable) {
	    
	            // Temos de ver quem está a tentar entrar no buraco.
	            // Se a lista toPush tiver algo, é esse objeto (ex: Cup).
	            // Se a lista estiver vazia, seria o Peixe (mas esta função só corre se já houver algo).
	            
	            GameObject objectTryingToEnter = toPush.isEmpty() ? this : toPush.get(toPush.size() - 1);
	            
	            // Verifica se o objeto (Cup) consegue passar na parede (Wall)
	            if (((Transposable) objAhead).isTransposableBy(objectTryingToEnter)) {
	                break; // Consegue passar! O movimento é válido.
	            } else {
	                return false; // O objeto é demasiado grande para o buraco.
	            }
	        }

	        // 3. Se é uma Parede Sólida normal (não tem buraco)
	        if (objAhead instanceof Untransposable) {
	            return false; // Bloqueado
	        }

	        // 4. Se é um objeto de empurrar (Pushable), continuamos a cadeia
	        if (objAhead instanceof Pushable) {
	            toPush.add(objAhead);

	            // Regras do Peixe Grande vs Pequeno
	            if (!(this instanceof Big) && toPush.size() > 1) {
	                return false; // Peixe pequeno só empurra 1
	            }
	            if ((this instanceof Big) && v.getY() != 0 && toPush.size() > 1) {
	                return false; // Peixe grande vertical (opcional)
	            }
	        } else {
	            // Se for outra coisa qualquer (ex: água não tratada como null), paramos
	            break; 
	        }

	        // Avança para a próxima posição
	        currentPos = currentPos.plus(v);
	    }

	    // Se a lista estiver vazia, não há nada para empurrar
	    if (toPush.isEmpty()) return false;

	    // Regra da Âncora
	    for (GameObject obj : toPush) {
	        if (obj instanceof Anchor && hasAlreadyMoved) {
	            return false;
	        }
	    }

	    // Mover tudo
	    for (int i = toPush.size() - 1; i >= 0; i--) {
	        GameObject obj = toPush.get(i);
	        obj.setPosition(obj.getPosition().plus(v));
	        if (obj instanceof Anchor) hasAlreadyMoved = true;
	    }

	    return true;
	}

	// Pequeno utilitário para ignorar água ao ver o que está na frente
	private GameObject getTopObjectAt(Point2D pos) {
	    for (GameObject obj : getRoom().getObjects()) {
	        if (obj.getPosition().equals(pos)) {
	            // Retorna o primeiro objeto sólido ou interativo que encontrar
	            if (obj instanceof Pushable || obj instanceof Untransposable) {
	                return obj;
	            }
	        }
	    }
	    return null; // Retorna null se for só Água ou Vazio
	}


    @Override
    public int getLayer() {
        return 2;
    }
}