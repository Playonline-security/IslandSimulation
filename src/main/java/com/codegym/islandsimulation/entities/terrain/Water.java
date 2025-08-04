package com.codegym.islandsimulation.entities.terrain;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Clase que representa agua en la simulación.
 * El agua es un elemento de terreno que no puede ser consumido por los animales.
 */
public class Water extends Organism {
    
    /**
     * Constructor del agua.
     * Inicializa agua con peso 50.0.
     */
    public Water() {
        super("💧", 50.0);
    }

    /**
     * El agua no es una planta (no puede ser consumida).
     * @return false
     */
    @Override
    public boolean isPlant() {
        return false;
    }
}