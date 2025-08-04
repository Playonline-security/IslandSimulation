package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.interfaces.Carnivore;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un pato en la simulación.
 * El pato es un depredador carnívoro que puede cazar pequeños animales.
 */
public class Duck extends Animal implements Carnivore {

    /**
     * Constructor del pato.
     * Inicializa un pato con peso 1.0, velocidad máxima 4 y necesidad de comida 0.15.
     */
    public Duck() {
        super("🦆", 1.0, 4, 0.15);
    }

    /**
     * Define las probabilidades de caza exitosa para cada tipo de presa.
     * @return Mapa con las probabilidades de caza para cada tipo de presa
     */
    @Override
    public Map<Class<? extends Organism>, Integer> getPreyChances() {
        Map<Class<? extends Organism>, Integer> preyMap = new HashMap<>();
        preyMap.put(Caterpillar.class, 90); // 90% de probabilidad de cazar una oruga
        return preyMap;
    }
}
