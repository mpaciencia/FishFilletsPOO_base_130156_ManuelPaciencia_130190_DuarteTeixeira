package interfaces;

import objects.GameObject;

public interface Transposable {
    // Indica se um GameObject pode atravessar este objeto (ex.: parede com buraco).
    // Útil para decidir se o movimento é bloqueado ou permitido.
    boolean isTransposableBy(GameObject gameObject);
}
