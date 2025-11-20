package interfaces;

import objects.GameObject;

//objetos que podem ser passados
public interface Transposable {
    //é passavel por quem?
    boolean isTransposableBy(GameObject gameObject);
}
