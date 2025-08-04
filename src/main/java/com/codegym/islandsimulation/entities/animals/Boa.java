package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.interfaces.Carnivore;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa una boa en la simulación.
 * La boa es un depredador carnívoro que puede cazar varios tipos de presas.
 */
public class Boa extends Animal implements Carnivore {

    /**
     * Constructor de la boa.
     * Inicializa una boa con peso 15.0, velocidad máxima 1 y necesidad de comida 3.0.
     */
    public Boa() {
        super("🐍", 15.0, 1, 3.0);
    }

    /**
     * Define las probabilidades de caza exitosa para cada tipo de presa.
     * @return Mapa con las probabilidades de caza para cada tipo de presa
     */
    @Override
    public Map<Class<? extends Organism>, Integer> getPreyChances() {
        Map<Class<? extends Organism>, Integer> preyMap = new HashMap<>();
        preyMap.put(Fox.class, 15);        // 15% de probabilidad de cazar un zorro
        preyMap.put(Rabbit.class, 20);     // 20% de probabilidad de cazar un conejo
        preyMap.put(Mouse.class, 40);      // 40% de probabilidad de cazar un ratón
        preyMap.put(Duck.class, 10);       // 10% de probabilidad de cazar un pato
        return preyMap;
    }
}
