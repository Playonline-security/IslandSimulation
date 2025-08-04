package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.interfaces.Carnivore;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un lobo en la simulación.
 * El lobo es un depredador carnívoro que puede cazar varios tipos de presas
 * con diferentes probabilidades de éxito.
 */
public class Wolf extends Animal implements Carnivore {

    /**
     * Constructor del lobo.
     * Inicializa un lobo con peso 50.0, velocidad máxima 3 y necesidad de comida 8.0.
     */
    public Wolf() {
        super("🐺", 50.0, 3, 8.0);
    }

    /**
     * Define las probabilidades de caza exitosa para cada tipo de presa.
     * Los valores representan porcentajes de éxito (0-100).
     * @return Mapa con las probabilidades de caza para cada tipo de presa
     */
    @Override
    public Map<Class<? extends Organism>, Integer> getPreyChances() {
        Map<Class<? extends Organism>, Integer> preyMap = new HashMap<>();
        preyMap.put(Horse.class, 10);      // 10% de probabilidad de cazar un caballo
        preyMap.put(Deer.class, 15);       // 15% de probabilidad de cazar un ciervo
        preyMap.put(Rabbit.class, 60);     // 60% de probabilidad de cazar un conejo
        preyMap.put(Mouse.class, 80);      // 80% de probabilidad de cazar un ratón
        preyMap.put(Goat.class, 60);       // 60% de probabilidad de cazar una cabra
        preyMap.put(Sheep.class, 70);      // 70% de probabilidad de cazar una oveja
        preyMap.put(Boar.class, 15);       // 15% de probabilidad de cazar un jabalí
        preyMap.put(Buffalo.class, 10);    // 10% de probabilidad de cazar un búfalo
        preyMap.put(Duck.class, 40);       // 40% de probabilidad de cazar un pato
        return preyMap;
    }
}
