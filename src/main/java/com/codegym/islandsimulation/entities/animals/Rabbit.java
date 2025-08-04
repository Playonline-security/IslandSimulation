package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.interfaces.Herbivore;

/**
 * Clase que representa un conejo en la simulación.
 * El conejo es un herbívoro que se alimenta de plantas y puede ser presa de depredadores.
 */
public class Rabbit extends Animal implements Herbivore {

    /**
     * Constructor del conejo.
     * Inicializa un conejo con peso 2.0, velocidad máxima 2 y necesidad de comida 0.45.
     */
    public Rabbit() {
        super("🐇", 2.0, 2, 0.45);
    }
}
