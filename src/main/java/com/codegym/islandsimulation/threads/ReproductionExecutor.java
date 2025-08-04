package com.codegym.islandsimulation.threads;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.animals.Animal;
import com.codegym.islandsimulation.map.IslandMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Ejecutor que maneja la reproducción de todos los animales en la simulación.
 * Utiliza un pool de hilos para procesar la reproducción de forma concurrente,
 * permitiendo que todos los animales intenten reproducirse simultáneamente.
 */
public class ReproductionExecutor {
    // El mapa de la isla donde están los animales
    private final IslandMap islandMap;
    
    // Pool de hilos para ejecutar la reproducción concurrentemente
    private final ExecutorService executorService;

    /**
     * Constructor del ejecutor de reproducción.
     * @param islandMap El mapa de la isla donde están los animales
     */
    public ReproductionExecutor(IslandMap islandMap) {
        this.islandMap = islandMap;
        // Crea un pool de hilos con el número de procesadores disponibles
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Procesa la reproducción de todos los animales en el mapa.
     * Crea tareas concurrentes para cada animal y las ejecuta en paralelo.
     */
    public void processAnimalReproduction() {
        List<Callable<Void>> tasks = new ArrayList<>();

        // Recorre el mapa buscando animales
        IntStream.range(0, islandMap.getRows()).forEach(row -> IntStream.range(0, islandMap.getCols()).forEach(col -> {
            Organism organism = islandMap.getOrganismAt(row, col);
            if (organism instanceof Animal animal) {
                // Captura las coordenadas finales para la tarea
                final int finalRow = row;
                final int finalCol = col;

                // Crea una tarea para que este animal intente reproducirse
                tasks.add(() -> {
                    animal.reproduce(islandMap, finalRow, finalCol);
                    return null;
                });
            }
        }));

        // Ejecuta todas las tareas de reproducción concurrentemente
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}