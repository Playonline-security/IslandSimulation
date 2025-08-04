package com.codegym.islandsimulation.threads;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Pool de hilos personalizado optimizado para la simulación de la isla.
 * Utiliza conceptos avanzados de concurrencia como ThreadFactory personalizada,
 * políticas de rechazo y monitoreo de hilos.
 */
public class SimulationThreadPool {
    
    // Pool principal para tareas de simulación
    private final ExecutorService simulationExecutor;
    
    // Pool separado para tareas de I/O (generación de HTML)
    private final ExecutorService ioExecutor;
    
    // Contador atómico para nombrar hilos
    private final AtomicInteger threadCounter = new AtomicInteger(1);
    
    // Estadísticas del pool
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private final AtomicInteger failedTasks = new AtomicInteger(0);

    /**
     * Constructor que inicializa los pools de hilos con configuración optimizada.
     */
    public SimulationThreadPool() {
        // Pool principal: número de hilos basado en CPU cores
        int corePoolSize = Math.max(2, Runtime.getRuntime().availableProcessors() - 1);
        int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        
        this.simulationExecutor = new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new SimulationThreadFactory("SimulationWorker"),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        // Pool de I/O: hilos dedicados para operaciones de archivo
        this.ioExecutor = Executors.newFixedThreadPool(2, 
            new SimulationThreadFactory("IOWorker"));
    }

    /**
     * Ejecuta una tarea de simulación en el pool principal.
     * @param task La tarea a ejecutar
     * @return Future que representa el resultado de la tarea
     */
    public <T> Future<T> submitSimulationTask(Callable<T> task) {
        return simulationExecutor.submit(() -> {
            try {
                T result = task.call();
                completedTasks.incrementAndGet();
                return result;
            } catch (Exception e) {
                failedTasks.incrementAndGet();
                throw e;
            }
        });
    }

    /**
     * Ejecuta una tarea de I/O en el pool dedicado.
     * @param task La tarea de I/O a ejecutar
     * @return Future que representa el resultado de la tarea
     */
    public <T> Future<T> submitIOTask(Callable<T> task) {
        return ioExecutor.submit(() -> {
            try {
                T result = task.call();
                completedTasks.incrementAndGet();
                return result;
            } catch (Exception e) {
                failedTasks.incrementAndGet();
                throw e;
            }
        });
    }

    /**
     * Ejecuta múltiples tareas de simulación concurrentemente.
     * @param tasks Lista de tareas a ejecutar
     * @return Lista de Futures con los resultados
     */
    public <T> List<Future<T>> submitAllSimulationTasks(List<Callable<T>> tasks) {
        try {
            return simulationExecutor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción durante la ejecución de tareas", e);
        }
    }

    /**
     * Obtiene estadísticas del pool de hilos.
     * @return String con información detallada del estado del pool
     */
    public String getPoolStatistics() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) simulationExecutor;
        return String.format(
            "Pool Stats - Active: %d, Pool Size: %d, Core Pool: %d, Max Pool: %d, " +
            "Completed: %d, Failed: %d, Queue Size: %d",
            tpe.getActiveCount(),
            tpe.getPoolSize(),
            tpe.getCorePoolSize(),
            tpe.getMaximumPoolSize(),
            completedTasks.get(),
            failedTasks.get(),
            tpe.getQueue().size()
        );
    }

    /**
     * Cierra los pools de hilos de forma ordenada.
     */
    public void shutdown() {
        simulationExecutor.shutdown();
        ioExecutor.shutdown();
        
        try {
            if (!simulationExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                simulationExecutor.shutdownNow();
            }
            if (!ioExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                ioExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            simulationExecutor.shutdownNow();
            ioExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * ThreadFactory personalizada para crear hilos con nombres descriptivos
     * y configuración optimizada.
     */
    private static class SimulationThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public SimulationThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
            
            // Configuración optimizada para hilos de simulación
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.setDaemon(false); // Los hilos de simulación no son daemon
            
            // Configurar un handler de excepciones personalizado
            thread.setUncaughtExceptionHandler((t, e) -> {
                System.err.println("Excepción no capturada en hilo " + t.getName() + ": " + e.getMessage());
                e.printStackTrace();
            });
            
            return thread;
        }
    }
} 