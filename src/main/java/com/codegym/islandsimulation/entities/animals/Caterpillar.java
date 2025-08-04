package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.interfaces.Herbivore;

/**
 * Clase que representa una oruga en la simulación.
 * La oruga es un herbívoro que se alimenta de plantas y tiene lógica especial de población.
 */
public class Caterpillar extends Animal implements Herbivore {

    /**
     * Constructor de la oruga.
     * Inicializa una oruga con peso 0.01, velocidad máxima 0 y necesidad de comida 0.0.
     */
    public Caterpillar() {
        super("🐛", 0.01, 0, 0.0);
    }
}
