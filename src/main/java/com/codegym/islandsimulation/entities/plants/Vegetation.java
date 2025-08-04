package com.codegym.islandsimulation.entities.plants;

/**
 * Clase que representa hierba en la simulación.
 * La hierba es una planta que puede ser consumida por herbívoros y revivir después de un tiempo.
 */
public class Vegetation extends Plant {

    /**
     * Constructor de la hierba.
     * Inicializa hierba con peso 1.0.
     */
    public Vegetation() {
        super("🌿", "Vegetation", 1.0);
    }
}
