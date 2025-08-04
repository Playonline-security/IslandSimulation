package com.codegym.islandsimulation.entities.terrain;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Clase que representa una roca en la simulación.
 * Las rocas son elementos de terreno que no pueden ser consumidos por los animales.
 */
public class Rock extends Organism {
    
    /**
     * Constructor de la roca.
     * Inicializa una roca con peso 50.0.
     */
    public Rock() {
        super("🪨", 50.0);
    }

    /**
     * Las rocas no son plantas (no pueden ser consumidas).
     * @return false
     */
    @Override
    public boolean isPlant() {
        return false;
    }
}