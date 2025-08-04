package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.interfaces.Herbivore;

/**
 * Clase que representa una cabra en la simulación.
 * La cabra es un herbívoro que se alimenta de plantas.
 */
public class Goat extends Animal implements Herbivore {

    /**
     * Constructor de la cabra.
     * Inicializa una cabra con peso 60.0, velocidad máxima 3 y necesidad de comida 10.0.
     */
    public Goat() {
        super("🐐", 60.0, 3, 10.0);
    }
}
