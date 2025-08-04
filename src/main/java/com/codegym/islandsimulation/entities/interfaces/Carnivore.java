package com.codegym.islandsimulation.entities.interfaces;

import com.codegym.islandsimulation.entities.Organism;

import java.util.Map;

/**
 * Interfaz que define el comportamiento de un animal carnívoro.
 * Los carnívoros pueden cazar otros animales según probabilidades específicas.
 */
public interface Carnivore {

    /**
     * Obtiene las probabilidades de caza exitosa para cada tipo de presa.
     * @return Un mapa donde la clave es el tipo de presa y el valor es la probabilidad (0-100)
     */
    Map<Class<? extends Organism>, Integer> getPreyChances();
}
