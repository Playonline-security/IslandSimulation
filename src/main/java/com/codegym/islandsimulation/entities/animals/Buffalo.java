package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.interfaces.Herbivore;

/**
 * Clase que representa un búfalo en la simulación.
 * El búfalo es un herbívoro que se alimenta de plantas.
 */
public class Buffalo extends Animal implements Herbivore {

    /**
     * Constructor del búfalo.
     * Inicializa un búfalo con peso 700.0, velocidad máxima 3 y necesidad de comida 100.0.
     */
    public Buffalo() {
        super("🐃", 700.0, 3, 100.0);
    }
}
