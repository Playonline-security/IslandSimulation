package com.codegym.islandsimulation.entities.terrain;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Clase que representa un árbol en la simulación.
 * Los árboles son elementos de terreno que no pueden ser consumidos por los animales.
 */
public class Tree extends Organism {
    
    /**
     * Constructor del árbol.
     * Inicializa un árbol con peso 50.0.
     */
    public Tree() {
        super("🌳", 50.0);
    }

    /**
     * Los árboles no son plantas (no pueden ser consumidos).
     * @return false
     */
    @Override
    public boolean isPlant() {
        return false;
    }
}