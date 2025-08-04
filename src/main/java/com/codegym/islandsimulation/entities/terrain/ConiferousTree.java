package com.codegym.islandsimulation.entities.terrain;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Clase que representa un árbol conífero en la simulación.
 * Los árboles coníferos son elementos de terreno que no pueden ser consumidos por los animales.
 */
public class ConiferousTree extends Organism {
    
    /**
     * Constructor del árbol conífero.
     * Inicializa un árbol conífero con peso 50.0.
     */
    public ConiferousTree() {
        super("🌲", 50.0);
    }

    /**
     * Los árboles coníferos no son plantas (no pueden ser consumidos).
     * @return false
     */
    @Override
    public boolean isPlant() {
        return false;
    }
}