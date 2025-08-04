package com.codegym.islandsimulation.threads;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Registro que representa un evento de consumo de planta en la simulación.
 * Contiene información sobre el herbívoro y la planta consumida.
 * @param herbivore El animal herbívoro que consume la planta
 * @param herbivoreRow Fila donde está el herbívoro
 * @param herbivoreCol Columna donde está el herbívoro
 * @param plantRow Fila donde está la planta consumida
 * @param plantCol Columna donde está la planta consumida
 */
public record EatingPlantEvent(Organism herbivore, int herbivoreRow, int herbivoreCol, int plantRow, int plantCol) {
}