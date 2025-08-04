package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.interfaces.Carnivore;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un oso en la simulación.
 * El oso es un depredador carnívoro que puede cazar varios tipos de presas.
 */
public class Bear extends Animal implements Carnivore {

    /**
     * Constructor del oso.
     * Inicializa un oso con peso 500.0, velocidad máxima 2 y necesidad de comida 80.0.
     */
    public Bear() {
        super("🐻", 500.0, 2, 80.0);
    }

    /**
     * Define las probabilidades de caza exitosa para cada tipo de presa.
     * @return Mapa con las probabilidades de caza para cada tipo de presa
     */
    @Override
    public Map<Class<? extends Organism>, Integer> getPreyChances() {
        Map<Class<? extends Organism>, Integer> preyMap = new HashMap<>();
        preyMap.put(Boa.class, 80);        // 80% de probabilidad de cazar una boa
        preyMap.put(Horse.class, 40);      // 40% de probabilidad de cazar un caballo
        preyMap.put(Deer.class, 80);       // 80% de probabilidad de cazar un ciervo
        preyMap.put(Rabbit.class, 80);     // 80% de probabilidad de cazar un conejo
        preyMap.put(Mouse.class, 90);      // 90% de probabilidad de cazar un ratón
        preyMap.put(Goat.class, 70);       // 70% de probabilidad de cazar una cabra
        preyMap.put(Sheep.class, 70);      // 70% de probabilidad de cazar una oveja
        preyMap.put(Boar.class, 50);       // 50% de probabilidad de cazar un jabalí
        preyMap.put(Buffalo.class, 20);    // 20% de probabilidad de cazar un búfalo
        preyMap.put(Duck.class, 10);       // 10% de probabilidad de cazar un pato
        return preyMap;
    }
}
