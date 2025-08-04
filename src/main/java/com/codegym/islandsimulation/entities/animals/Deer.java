package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.interfaces.Herbivore;

/**
 * Clase que representa un ciervo en la simulación.
 * El ciervo es un herbívoro que se alimenta de plantas.
 */
public class Deer extends Animal implements Herbivore {

    /**
     * Constructor del ciervo.
     * Inicializa un ciervo con peso 300.0, velocidad máxima 4 y necesidad de comida 50.0.
     */
    public Deer() {
        super("🦌", 300.0, 4, 50.0);
    }
}
