package com.codegym.islandsimulation.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registro central de eventos de la simulación.
 * Mantiene listas de todos los eventos que ocurren durante un ciclo de simulación:
 * cazas, consumo de plantas, reproducciones y alarmas de población.
 * Utiliza colecciones thread-safe para manejar eventos concurrentes.
 */
public class SimulationEventRegistry {
    // Lista de eventos de caza (thread-safe)
    private static final List<HuntingEvent> huntingEvents = Collections.synchronizedList(new ArrayList<>());
    
    // Lista de eventos de consumo de plantas
    private static final List<EatingPlantEvent> eatingPlantEvents = new ArrayList<>();
    
    // Lista de eventos de reproducción (thread-safe)
    private static final List<ReproductionEvent> reproductionEvents = new CopyOnWriteArrayList<>();
    
    // Lista de alarmas de población (thread-safe)
    private static final List<PopulationAlarmEvent> populationAlarmEvents = new CopyOnWriteArrayList<>();

    /**
     * Añade un evento de caza al registro.
     * @param event El evento de caza a registrar
     */
    public static void addHuntingEvent(HuntingEvent event) {
        huntingEvents.add(event);
    }

    /**
     * Obtiene una copia de todos los eventos de caza registrados.
     * @return Lista de eventos de caza
     */
    public static List<HuntingEvent> getHuntingEvents() {
        return new ArrayList<>(huntingEvents);
    }

    /**
     * Limpia todos los eventos de caza del registro.
     */
    public static void clearHuntingEvents() {
        huntingEvents.clear();
    }

    /**
     * Añade un evento de consumo de planta al registro.
     * @param event El evento de consumo de planta a registrar
     */
    public static void addEatingPlantEvent(EatingPlantEvent event) {
        eatingPlantEvents.add(event);
    }

    /**
     * Obtiene todos los eventos de consumo de plantas registrados.
     * @return Lista de eventos de consumo de plantas
     */
    public static List<EatingPlantEvent> getEatingPlantEvents() {
        return eatingPlantEvents;
    }

    /**
     * Limpia todos los eventos de consumo de plantas del registro.
     */
    public static void clearEatingPlantEvents() {
        eatingPlantEvents.clear();
    }

    /**
     * Añade un evento de reproducción al registro.
     * @param event El evento de reproducción a registrar
     */
    public static void addReproductionEvent(ReproductionEvent event) {
        reproductionEvents.add(event);
    }

    /**
     * Obtiene todos los eventos de reproducción registrados.
     * @return Lista de eventos de reproducción
     */
    public static List<ReproductionEvent> getReproductionEvents() {
        return reproductionEvents;
    }

    /**
     * Limpia todos los eventos de reproducción del registro.
     */
    public static void clearReproductionEvents() {
        reproductionEvents.clear();
    }

    /**
     * Añade una alarma de población al registro.
     * @param event La alarma de población a registrar
     */
    public static void addPopulationAlarmEvent(PopulationAlarmEvent event) {
        populationAlarmEvents.add(event);
    }

    /**
     * Obtiene una copia de todas las alarmas de población registradas.
     * @return Lista de alarmas de población
     */
    public static List<PopulationAlarmEvent> getPopulationAlarmEvents() {
        return new ArrayList<>(populationAlarmEvents);
    }

    /**
     * Limpia todas las alarmas de población del registro.
     */
    public static void clearPopulationAlarmEvents() {
        populationAlarmEvents.clear();
    }
}