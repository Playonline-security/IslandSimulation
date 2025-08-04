package com.codegym.islandsimulation.utils;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.animals.*;
import com.codegym.islandsimulation.entities.plants.Plant;
import com.codegym.islandsimulation.entities.terrain.*;
import com.codegym.islandsimulation.map.IslandMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase utilitaria que proporciona métodos para trabajar con el mapa de la isla.
 * Incluye funciones para buscar organismos, encontrar posiciones adyacentes y mapear emojis.
 */
public class MapUtils {

    // Mapa que asocia cada clase de organismo con su emoji representativo
    private static final Map<Class<? extends Organism>, String> EMOJI_MAP = new HashMap<>();

    // Inicialización estática del mapa de emojis
    static {
        EMOJI_MAP.put(Bear.class, "🐻");
        EMOJI_MAP.put(Boar.class, "🐗");
        EMOJI_MAP.put(Caterpillar.class, "🐛");
        EMOJI_MAP.put(Deer.class, "🦌");
        EMOJI_MAP.put(Duck.class, "🦆");
        EMOJI_MAP.put(Eagle.class, "🦅");
        EMOJI_MAP.put(Fox.class, "🦊");
        EMOJI_MAP.put(Goat.class, "🐐");
        EMOJI_MAP.put(Horse.class, "🐎");
        EMOJI_MAP.put(Mouse.class, "🐭");
        EMOJI_MAP.put(Rabbit.class, "🐇");
        EMOJI_MAP.put(Sheep.class, "🐑");
        EMOJI_MAP.put(Buffalo.class, "🐃");
        EMOJI_MAP.put(Boa.class, "🐍");
        EMOJI_MAP.put(Wolf.class, "🐺");
        EMOJI_MAP.put(Water.class, "💧");
        EMOJI_MAP.put(PalmTree.class, "🌴");
        EMOJI_MAP.put(ConiferousTree.class, "🌲");
        EMOJI_MAP.put(Tree.class, "🌳");
        EMOJI_MAP.put(Rock.class, "🪨");
        EMOJI_MAP.put(Plant.class, "🌿");
    }

    // Direcciones adyacentes (8 direcciones: arriba, abajo, izquierda, derecha y diagonales)
    private static final int[][] ADJACENT_DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    /**
     * Encuentra la posición de un organismo específico en el mapa.
     * @param map El mapa donde buscar
     * @param target El organismo a buscar
     * @return Array con [fila, columna] de la posición, o null si no se encuentra
     */
    public static int[] findOrganismPosition(IslandMap map, Organism target) {
        if (target == null) return null;

        // Busca en el mapa
        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                Organism organism = map.getOrganismAt(row, col);
                if (organism != null && organism.getId().equals(target.getId())) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los organismos adyacentes a una posición específica.
     * @param map El mapa donde buscar
     * @param row Fila de la posición central
     * @param col Columna de la posición central
     * @return Lista de organismos adyacentes (no incluye posiciones vacías)
     */
    public static List<Organism> getAdjacentOrganisms(IslandMap map, int row, int col) {
        List<Organism> neighbors = new ArrayList<>();

        // Revisa todas las direcciones adyacentes
        for (int[] dir : ADJACENT_DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (map.isValidPosition(newRow, newCol)) {
                Organism organism = map.getOrganismAt(newRow, newCol);
                if (organism != null) {
                    neighbors.add(organism);
                }
            }
        }

        return neighbors;
    }

    /**
     * Encuentra una posición vacía adyacente a una posición específica.
     * @param map El mapa donde buscar
     * @param row Fila de la posición central
     * @param col Columna de la posición central
     * @return Array con [fila, columna] de la posición vacía, o null si no hay ninguna
     */
    public static int[] findEmptyAdjacentPosition(IslandMap map, int row, int col) {
        // Revisa todas las direcciones adyacentes
        for (int[] dir : ADJACENT_DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (map.isValidPosition(newRow, newCol) && map.getOrganismAt(newRow, newCol) == null) {
                return new int[]{newRow, newCol};
            }
        }
        return null;
    }

    /**
     * Obtiene el emoji asociado a una clase de organismo.
     * @param species La clase del organismo
     * @return El emoji representativo del organismo
     */
    public static String getEmojiForClass(Class<? extends Organism> species) {
        String emoji = EMOJI_MAP.get(species);
        if (emoji != null) {
            return emoji;
        }

        // Maneja el caso de las plantas de forma genérica
        if (Plant.class.isAssignableFrom(species)) {
            return EMOJI_MAP.get(Plant.class);
        }

        return "❔"; // Emoji por defecto para organismos no mapeados
    }
}