package com.codegym.islandsimulation.threads;

import com.codegym.islandsimulation.map.IslandMap;
import com.codegym.islandsimulation.entities.Organism;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Sincronizador optimizado para el mapa de la isla.
 * Utiliza ReadWriteLock para permitir múltiples lecturas concurrentes
 * mientras mantiene la exclusividad en las escrituras.
 */
public class SimulationSynchronizer {
    
    // ReadWriteLock para optimizar el acceso concurrente al mapa
    private final ReadWriteLock mapLock = new ReentrantReadWriteLock();
    
    // Lock de lectura para operaciones de solo lectura
    private final Lock readLock = mapLock.readLock();
    
    // Lock de escritura para operaciones de modificación
    private final Lock writeLock = mapLock.writeLock();
    
    // Timeout para evitar deadlocks
    private static final long LOCK_TIMEOUT = 1000; // 1 segundo

    /**
     * Ejecuta una operación de solo lectura en el mapa.
     * Permite múltiples lecturas concurrentes.
     * @param operation La operación de lectura a ejecutar
     * @return El resultado de la operación
     */
    public <T> T executeReadOperation(ReadOperation<T> operation) {
        boolean locked = false;
        try {
            locked = readLock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
            if (!locked) {
                throw new RuntimeException("Timeout al obtener lock de lectura");
            }
            return operation.execute();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción durante operación de lectura", e);
        } finally {
            if (locked) {
                readLock.unlock();
            }
        }
    }

    /**
     * Ejecuta una operación de escritura en el mapa.
     * Garantiza exclusividad total durante la escritura.
     * @param operation La operación de escritura a ejecutar
     */
    public void executeWriteOperation(WriteOperation operation) {
        boolean locked = false;
        try {
            locked = writeLock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
            if (!locked) {
                throw new RuntimeException("Timeout al obtener lock de escritura");
            }
            operation.execute();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción durante operación de escritura", e);
        } finally {
            if (locked) {
                writeLock.unlock();
            }
        }
    }

    /**
     * Ejecuta una operación de escritura que retorna un valor.
     * @param operation La operación de escritura a ejecutar
     * @return El resultado de la operación
     */
    public <T> T executeWriteOperation(WriteOperationWithResult<T> operation) {
        boolean locked = false;
        try {
            locked = writeLock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
            if (!locked) {
                throw new RuntimeException("Timeout al obtener lock de escritura");
            }
            return operation.execute();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción durante operación de escritura", e);
        } finally {
            if (locked) {
                writeLock.unlock();
            }
        }
    }

    /**
     * Obtiene información sobre el estado de los locks.
     * @return String con información de los locks
     */
    public String getLockInfo() {
        ReentrantReadWriteLock rwLock = (ReentrantReadWriteLock) mapLock;
        return String.format(
            "Lock Info - Read Lock Count: %d, Write Lock Count: %d, " +
            "Read Lock Queue Length: %d, Write Lock Queue Length: %d",
            rwLock.getReadLockCount(),
            rwLock.getWriteHoldCount(),
            rwLock.getQueueLength(),
            rwLock.getQueueLength()
        );
    }

    /**
     * Interfaz funcional para operaciones de solo lectura.
     */
    @FunctionalInterface
    public interface ReadOperation<T> {
        T execute();
    }

    /**
     * Interfaz funcional para operaciones de escritura.
     */
    @FunctionalInterface
    public interface WriteOperation {
        void execute();
    }

    /**
     * Interfaz funcional para operaciones de escritura con resultado.
     */
    @FunctionalInterface
    public interface WriteOperationWithResult<T> {
        T execute();
    }
} 