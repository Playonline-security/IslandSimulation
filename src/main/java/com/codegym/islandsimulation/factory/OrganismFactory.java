package com.codegym.islandsimulation.factory;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Factoría para crear instancias de organismos.
 * Utiliza reflexión para crear organismos de cualquier tipo especificado.
 */
public class OrganismFactory {

    /**
     * Crea una nueva instancia de un organismo del tipo especificado.
     * @param clazz La clase del organismo a crear
     * @return Una nueva instancia del organismo, o null si hay un error
     */
    public static Organism createOrganism(Class<? extends Organism> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println("❌ Error al crear organismo: " + clazz.getSimpleName());
            return null;
        }
    }
}
