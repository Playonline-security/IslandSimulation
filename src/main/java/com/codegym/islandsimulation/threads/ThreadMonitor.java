package com.codegym.islandsimulation.threads;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitor de hilos que proporciona información detallada sobre el estado
 * de los hilos en tiempo real. Utiliza ThreadMXBean para obtener métricas
 * del sistema de hilos.
 */
public class ThreadMonitor {
    
    // Bean para obtener información del sistema de hilos
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    
    // Scheduler para ejecutar el monitoreo periódicamente
    private final ScheduledExecutorService monitorScheduler = Executors.newSingleThreadScheduledExecutor();
    
    // Contadores atómicos para estadísticas
    private final AtomicLong totalCpuTime = new AtomicLong(0);
    private final AtomicLong totalUserTime = new AtomicLong(0);
    private final AtomicLong deadlockCount = new AtomicLong(0);
    
    // Flag para controlar el monitoreo
    private volatile boolean isMonitoring = false;

    /**
     * Inicia el monitoreo de hilos en tiempo real.
     */
    public void startMonitoring() {
        if (isMonitoring) {
            return;
        }
        
        isMonitoring = true;
        System.out.println("🔍 Iniciando monitoreo de hilos...");
        
        // Ejecuta el monitoreo cada 5 segundos
        monitorScheduler.scheduleAtFixedRate(this::performMonitoring, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Detiene el monitoreo de hilos.
     */
    public void stopMonitoring() {
        if (!isMonitoring) {
            return;
        }
        
        isMonitoring = false;
        monitorScheduler.shutdown();
        System.out.println("🛑 Monitoreo de hilos detenido.");
    }

    /**
     * Realiza el monitoreo actual de los hilos.
     */
    private void performMonitoring() {
        try {
            // Obtiene información de todos los hilos
            ThreadInfo[] threadInfos = threadBean.dumpAllThreads(false, false);
            
            // Analiza deadlocks
            long[] deadlockedThreads = threadBean.findDeadlockedThreads();
            if (deadlockedThreads != null) {
                deadlockCount.incrementAndGet();
                System.err.println("🚨 ¡DEADLOCK DETECTADO! Threads involucrados: " + deadlockedThreads.length);
            }
            
            // Calcula estadísticas
            int activeThreads = 0;
            int blockedThreads = 0;
            int waitingThreads = 0;
            
            for (ThreadInfo info : threadInfos) {
                if (info.getThreadState() == Thread.State.RUNNABLE) {
                    activeThreads++;
                } else if (info.getThreadState() == Thread.State.BLOCKED) {
                    blockedThreads++;
                } else if (info.getThreadState() == Thread.State.WAITING || 
                          info.getThreadState() == Thread.State.TIMED_WAITING) {
                    waitingThreads++;
                }
            }
            
            // Actualiza tiempos de CPU
            long[] threadIds = threadBean.getAllThreadIds();
            long cpuTime = 0;
            long userTime = 0;
            
            for (long threadId : threadIds) {
                cpuTime += threadBean.getThreadCpuTime(threadId);
                userTime += threadBean.getThreadUserTime(threadId);
            }
            
            totalCpuTime.set(cpuTime);
            totalUserTime.set(userTime);
            
            // Imprime reporte
            printMonitoringReport(threadInfos.length, activeThreads, blockedThreads, waitingThreads);
            
        } catch (Exception e) {
            System.err.println("Error durante el monitoreo: " + e.getMessage());
        }
    }

    /**
     * Imprime el reporte de monitoreo.
     */
    private void printMonitoringReport(int totalThreads, int activeThreads, 
                                     int blockedThreads, int waitingThreads) {
        System.out.println("📊 === REPORTE DE HILOS ===");
        System.out.printf("Total de hilos: %d | Activos: %d | Bloqueados: %d | Esperando: %d%n",
                         totalThreads, activeThreads, blockedThreads, waitingThreads);
        System.out.printf("Tiempo total CPU: %.2f ms | Tiempo usuario: %.2f ms%n",
                         totalCpuTime.get() / 1_000_000.0, totalUserTime.get() / 1_000_000.0);
        System.out.printf("Deadlocks detectados: %d%n", deadlockCount.get());
        System.out.println("==========================");
    }

    /**
     * Obtiene información detallada de un hilo específico.
     * @param threadName Nombre del hilo a analizar
     * @return Información detallada del hilo
     */
    public String getThreadDetails(String threadName) {
        ThreadInfo[] threadInfos = threadBean.dumpAllThreads(false, false);
        
        for (ThreadInfo info : threadInfos) {
            if (info.getThreadName().contains(threadName)) {
                return String.format(
                    "Hilo: %s | Estado: %s | CPU Time: %.2f ms | Bloqueado por: %s",
                    info.getThreadName(),
                    info.getThreadState(),
                    threadBean.getThreadCpuTime(info.getThreadId()) / 1_000_000.0,
                    info.getLockName() != null ? info.getLockName() : "N/A"
                );
            }
        }
        return "Hilo no encontrado: " + threadName;
    }

    /**
     * Obtiene un resumen de estadísticas del sistema de hilos.
     * @return String con estadísticas resumidas
     */
    public String getThreadSummary() {
        ThreadInfo[] threadInfos = threadBean.dumpAllThreads(false, false);
        int totalThreads = threadInfos.length;
        int activeThreads = 0;
        int blockedThreads = 0;
        
        for (ThreadInfo info : threadInfos) {
            if (info.getThreadState() == Thread.State.RUNNABLE) {
                activeThreads++;
            } else if (info.getThreadState() == Thread.State.BLOCKED) {
                blockedThreads++;
            }
        }
        
        return String.format(
            "Thread Summary - Total: %d, Active: %d, Blocked: %d, CPU Time: %.2f ms",
            totalThreads, activeThreads, blockedThreads, 
            totalCpuTime.get() / 1_000_000.0
        );
    }

    /**
     * Verifica si hay deadlocks en el sistema.
     * @return true si hay deadlocks, false en caso contrario
     */
    public boolean hasDeadlocks() {
        long[] deadlockedThreads = threadBean.findDeadlockedThreads();
        return deadlockedThreads != null && deadlockedThreads.length > 0;
    }

    /**
     * Obtiene información de deadlocks si existen.
     * @return String con información de deadlocks
     */
    public String getDeadlockInfo() {
        long[] deadlockedThreads = threadBean.findDeadlockedThreads();
        if (deadlockedThreads == null) {
            return "No hay deadlocks detectados";
        }
        
        StringBuilder info = new StringBuilder("Deadlocks detectados:\n");
        for (long threadId : deadlockedThreads) {
            ThreadInfo threadInfo = threadBean.getThreadInfo(threadId);
            if (threadInfo != null) {
                info.append(String.format("  - Hilo: %s (ID: %d) | Estado: %s\n",
                    threadInfo.getThreadName(), threadId, threadInfo.getThreadState()));
            }
        }
        return info.toString();
    }
} 