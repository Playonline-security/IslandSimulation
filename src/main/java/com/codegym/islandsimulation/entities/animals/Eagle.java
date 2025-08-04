package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.interfaces.Carnivore;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un águila en la simulación.
 * El águila es un depredador carnívoro que puede cazar varios tipos de presas.
 */
public class Eagle extends Animal implements Carnivore {

    /**
     * Constructor del águila.
     * Inicializa un águila con peso 6.0, velocidad máxima 3 y necesidad de comida 1.0.
     */
    public Eagle() {
        super("🦅", 6.0, 3, 1.0);
    }

    /**
     * Define las probabilidades de caza exitosa para cada tipo de presa.
     * @return Mapa con las probabilidades de caza para cada tipo de presa
     */
    @Override
    public Map<Class<? extends Organism>, Integer> getPreyChances() {
        Map<Class<? extends Organism>, Integer> preyMap = new HashMap<>();
        preyMap.put(Fox.class, 10);        // 10% de probabilidad de cazar un zorro
        preyMap.put(Rabbit.class, 90);     // 90% de probabilidad de cazar un conejo
        preyMap.put(Mouse.class, 90);      // 90% de probabilidad de cazar un ratón
        preyMap.put(Duck.class, 80);       // 80% de probabilidad de cazar un pato
        return preyMap;
    }
}
