package com.codegym.islandsimulation.threads;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.animals.Animal;
import com.codegym.islandsimulation.map.IslandMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ejecutor que maneja el movimiento de todos los animales en la simulación.
 * Utiliza un pool de hilos para procesar los movimientos de forma concurrente,
 * mejorando el rendimiento de la simulación.
 */
public class MovementExecutor {
    // El mapa de la isla donde se mueven los animales
    private final IslandMap map;
    
    // Pool de hilos para ejecutar los movimientos concurrentemente
    private final ExecutorService executor;

    /**
     * Constructor del ejecutor de movimientos.
     * @param map El mapa de la isla donde se mueven los animales
     */
    public MovementExecutor(IslandMap map) {
        this.map = map;
        // Crea un pool de hilos con el número de procesadores disponibles
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Procesa el movimiento de todos los animales en el mapa.
     * Crea tareas concurrentes para cada animal y las ejecuta en paralelo.
     */
    public void processAnimalMovements() {
        List<Callable<Void>> tasks = new ArrayList<>();

        // Recorre todo el mapa buscando animales
        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                Organism organism = map.getOrganismAt(row, col);
                if (organism instanceof Animal animal) {
                    // Captura las coordenadas finales para la tarea
                    final int finalRow = row;
                    final int finalCol = col;
                    
                    // Crea una tarea para mover este animal
                    tasks.add(() -> {
                        synchronized (map) {
                            animal.move(map, finalRow, finalCol);
                        }
                        return null;
                    });
                }
            }
        }

        // Ejecuta todas las tareas de movimiento concurrentemente
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
