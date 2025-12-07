package interfaces;

import objects.GameObject;

public interface Pushable {
    // Indica se este objeto pode ser empurrado por um certo GameObject (ex.: BigFish, SmallFish).
    // Não move o objeto — apenas diz se é permitido tentar empurrar.
    boolean isPushableBy(GameObject gameObject);
}
