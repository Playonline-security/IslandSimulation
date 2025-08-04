package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.interfaces.Herbivore;

/**
 * Clase que representa un caballo en la simulación.
 * El caballo es un herbívoro que se alimenta de plantas.
 */
public class Horse extends Animal implements Herbivore {

    /**
     * Constructor del caballo.
     * Inicializa un caballo con peso 400.0, velocidad máxima 4 y necesidad de comida 60.0.
     */
    public Horse() {
        super("🐎", 400.0, 4, 60.0);
    }
}
