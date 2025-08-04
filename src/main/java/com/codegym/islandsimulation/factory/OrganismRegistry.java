package com.codegym.islandsimulation.factory;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.animals.*;
import com.codegym.islandsimulation.entities.plants.Vegetation;
import com.codegym.islandsimulation.entities.terrain.*;

import java.util.*;

/**
 * Registro central de todos los tipos de organismos disponibles en la simulación.
 * Mantiene un mapeo entre nombres y clases de organismos para facilitar su creación.
 */
public class OrganismRegistry {

    // Mapa que asocia nombres de organismos con sus clases correspondientes
    private static final Map<String, Class<? extends Organism>> organismMap = new HashMap<>();

    // Inicialización estática: registra todos los tipos de organismos disponibles
    static {
        // Registra todos los animales
        register("Lobo", Wolf.class);
        register("Boa", Boa.class);
        register("Zorro", Fox.class);
        register("Oso", Bear.class);
        register("Águila", Eagle.class);
        register("Caballo", Horse.class);
        register("Ciervo", Deer.class);
        register("Conejo", Rabbit.class);
        register("Ratón", Mouse.class);
        register("Cabra", Goat.class);
        register("Oveja", Sheep.class);
        register("Jabalí", Boar.class);
        register("Búfalo", Buffalo.class);
        register("Pato", Duck.class);
        register("Oruga", Caterpillar.class);
        
        // Registra plantas y terreno
        register("Hierba", Vegetation.class);
        register("Árbol", Tree.class);
        register("Roca", Rock.class);
        register("Agua", Water.class);
        register("Hongo", Mushroom.class);
        register("Palmera", PalmTree.class);
        register("Árbol conífero", ConiferousTree.class);
        register("Hierba de hongo", MushroomGrass.class);
    }

    /**
     * Registra un tipo de organismo en el registro.
     * @param name El nombre del organismo
     * @param clazz La clase del organismo
     */
    private static void register(String name, Class<? extends Organism> clazz) {
        organismMap.put(name, clazz);
    }

    /**
     * Obtiene todos los tipos de organismos registrados.
     * @return Lista de todas las clases de organismos disponibles
     */
    public static List<Class<? extends Organism>> getAllOrganismTypes() {
        return new ArrayList<>(organismMap.values());
    }

}
