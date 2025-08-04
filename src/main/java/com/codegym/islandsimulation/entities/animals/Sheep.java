package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.interfaces.Herbivore;

/**
 * Clase que representa una oveja en la simulación.
 * La oveja es un herbívoro que se alimenta de plantas.
 */
public class Sheep extends Animal implements Herbivore {

    /**
     * Constructor de la oveja.
     * Inicializa una oveja con peso 70.0, velocidad máxima 3 y necesidad de comida 15.0.
     */
    public Sheep() {
        super("🐑", 70.0, 3, 15.0);
    }
}
