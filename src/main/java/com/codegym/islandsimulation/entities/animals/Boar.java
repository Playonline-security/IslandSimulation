package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.interfaces.Carnivore;
import com.codegym.islandsimulation.entities.interfaces.Herbivore;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un jabalí en la simulación.
 * El jabalí es un herbívoro que se alimenta de plantas.
 */
public class Boar extends Animal implements Herbivore, Carnivore {

    /**
     * Constructor del jabalí.
     * Inicializa un jabalí con peso 400.0, velocidad máxima 2 y necesidad de comida 50.0.
     */
    public Boar() {
        super("🐗", 400.0, 2, 50.0);
    }

    @Override
    public Map<Class<? extends Organism>, Integer> getPreyChances() {
        Map<Class<? extends Organism>, Integer> preyMap = new HashMap<>();
        preyMap.put(Mouse.class, 50);
        preyMap.put(Caterpillar.class, 90);
        return preyMap;
    }
}
