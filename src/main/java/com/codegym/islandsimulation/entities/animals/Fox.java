package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.interfaces.Carnivore;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un zorro en la simulación.
 * El zorro es un depredador carnívoro que puede cazar varios tipos de presas.
 */
public class Fox extends Animal implements Carnivore {

    /**
     * Constructor del zorro.
     * Inicializa un zorro con peso 8.0, velocidad máxima 2 y necesidad de comida 2.0.
     */
    public Fox() {
        super("🦊", 8.0, 2, 2.0);
    }

    /**
     * Define las probabilidades de caza exitosa para cada tipo de presa.
     * @return Mapa con las probabilidades de caza para cada tipo de presa
     */
    @Override
    public Map<Class<? extends Organism>, Integer> getPreyChances() {
        Map<Class<? extends Organism>, Integer> preyMap = new HashMap<>();
        preyMap.put(Rabbit.class, 70);     // 70% de probabilidad de cazar un conejo
        preyMap.put(Mouse.class, 90);      // 90% de probabilidad de cazar un ratón
        preyMap.put(Duck.class, 60);       // 60% de probabilidad de cazar un pato
        preyMap.put(Caterpillar.class, 40); // 40% de probabilidad de cazar una oruga
        return preyMap;
    }
}
