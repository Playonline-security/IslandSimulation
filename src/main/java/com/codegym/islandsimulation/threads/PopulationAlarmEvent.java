package com.codegym.islandsimulation.threads;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Clase que representa una alarma de población en la simulación.
 * Se activa cuando la población de un tipo de animal está en niveles críticos.
 */
public class PopulationAlarmEvent {
    // El tipo de animal cuya población está en peligro
    private final Class<? extends Organism> animalType;

    /**
     * Constructor de la alarma de población.
     * @param animalType El tipo de animal cuya población está en peligro
     */
    public PopulationAlarmEvent(Class<? extends Organism> animalType) {
        this.animalType = animalType;
    }

    /**
     * Obtiene el tipo de animal de la alarma.
     * @return La clase del animal cuya población está en peligro
     */
    public Class<? extends Organism> getAnimalType() {
        return animalType;
    }
}