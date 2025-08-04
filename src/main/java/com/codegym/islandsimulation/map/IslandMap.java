package com.codegym.islandsimulation.map;

import com.codegym.islandsimulation.entities.Organism;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el mapa de la isla donde viven todos los organismos.
 * El mapa es una cuadrícula 2D donde cada celda puede contener un organismo o estar vacía.
 */
public class IslandMap {

    // Número de filas del mapa
    private final int rows;
    
    // Número de columnas del mapa
    private final int cols;
    
    // Matriz 2D que almacena los organismos en cada posición
    private final Organism[][] grid;

    /**
     * Constructor del mapa de la isla.
     * @param rows Número de filas del mapa
     * @param cols Número de columnas del mapa
     */
    public IslandMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Organism[rows][cols];
    }

    /**
     * Obtiene el número de filas del mapa.
     * @return El número de filas
     */
    public int getRows() {
        return rows;
    }

    /**
     * Obtiene el número de columnas del mapa.
     * @return El número de columnas
     */
    public int getCols() {
        return cols;
    }

    /**
     * Obtiene una lista de todos los organismos presentes en el mapa.
     * @return Lista de todos los organismos no nulos
     */
    public List<Organism> getOrganisms() {
        List<Organism> organisms = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != null) {
                    organisms.add(grid[i][j]);
                }
            }
        }
        return organisms;
    }

    /**
     * Obtiene el organismo en una posición específica del mapa.
     * @param row Fila de la posición
     * @param col Columna de la posición
     * @return El organismo en esa posición, o null si está vacía o la posición es inválida
     */
    public Organism getOrganismAt(int row, int col) {
        if (isValidPosition(row, col)) {
            return grid[row][col];
        }
        return null;
    }

    /**
     * Coloca un organismo en una posición específica del mapa.
     * @param row Fila de la posición
     * @param col Columna de la posición
     * @param organism El organismo a colocar (puede ser null para vaciar la posición)
     */
    public void setOrganismAt(int row, int col, Organism organism) {
        if (isValidPosition(row, col)) {
            grid[row][col] = organism;
        }
    }

    /**
     * Verifica si una posición es válida dentro del mapa.
     * @param row Fila a verificar
     * @param col Columna a verificar
     * @return true si la posición está dentro de los límites del mapa
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}
