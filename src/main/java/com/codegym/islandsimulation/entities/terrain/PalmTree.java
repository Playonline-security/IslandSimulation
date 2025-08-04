package com.codegym.islandsimulation.entities.terrain;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Clase que representa una palmera en la simulación.
 * Las palmeras son elementos de terreno que no pueden ser consumidos por los animales.
 */
public class PalmTree extends Organism {
    
    /**
     * Constructor de la palmera.
     * Inicializa una palmera con peso 50.0.
     */
    public PalmTree() {
        super("🌴", 50.0);
    }

    /**
     * Las palmeras no son plantas (no pueden ser consumidas).
     * @return false
     */
    @Override
    public boolean isPlant() {
        return false;
    }
}