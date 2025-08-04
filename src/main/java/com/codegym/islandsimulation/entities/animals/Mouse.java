package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.interfaces.Carnivore;
import com.codegym.islandsimulation.entities.interfaces.Herbivore;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un ratón en la simulación.
 * El ratón es un herbívoro que se alimenta de plantas.
 */
public class Mouse extends Animal implements Herbivore {

    /**
     * Constructor del ratón.
     * Inicializa un ratón con peso 0.05, velocidad máxima 1 y necesidad de comida 0.01.
     */
    public Mouse() {
        super("🐭", 0.05, 1, 0.01);
    }

    public Map<Class<? extends Organism>, Integer> getPreyChances() {
        Map<Class<? extends Organism>, Integer> preyMap = new HashMap<>();
        preyMap.put(Caterpillar.class, 90);
        return preyMap;
    }
}
