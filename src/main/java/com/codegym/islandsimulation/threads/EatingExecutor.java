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
 * Ejecutor que maneja la alimentación de todos los animales en la simulación.
 * Utiliza un pool de hilos para procesar la alimentación de forma concurrente,
 * permitiendo que todos los animales busquen comida simultáneamente.
 */
public class EatingExecutor {
    // El mapa de la isla donde están los animales y su comida
    private final IslandMap map;
    
    // Pool de hilos para ejecutar la alimentación concurrentemente
    private final ExecutorService executor;

    /**
     * Constructor del ejecutor de alimentación.
     * @param map El mapa de la isla donde están los animales y su comida
     */
    public EatingExecutor(IslandMap map) {
        this.map = map;
        // Crea un pool de hilos con el número de procesadores disponibles
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Procesa la alimentación de todos los animales en el mapa.
     * Crea tareas concurrentes para cada animal y las ejecuta en paralelo.
     */
    public void processAnimalEating() {
        List<Callable<Void>> tasks = new ArrayList<>();

        // Recorre todo el mapa buscando animales
        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                Organism organism = map.getOrganismAt(row, col);
                if (organism instanceof Animal) {
                    Animal animal = (Animal) organism;
                    // Captura las coordenadas finales para la tarea
                    final int finalRow = row;
                    final int finalCol = col;
                    
                    // Crea una tarea para que este animal coma
                    tasks.add(() -> {
                        animal.eat(map, finalRow, finalCol);
                        return null;
                    });
                }
            }
        }

        // Ejecuta todas las tareas de alimentación concurrentemente
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}