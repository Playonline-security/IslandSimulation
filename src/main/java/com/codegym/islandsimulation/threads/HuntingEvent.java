package com.codegym.islandsimulation.threads;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Registro que representa un evento de caza en la simulación.
 * Contiene información sobre el depredador, la presa y sus posiciones.
 * @param predator El animal depredador que realiza la caza
 * @param prey El animal presa que es cazado
 * @param predatorRow Fila donde está el depredador
 * @param predatorCol Columna donde está el depredador
 * @param preyRow Fila donde está la presa
 * @param preyCol Columna donde está la presa
 */
public record HuntingEvent(Organism predator,
                           Organism prey,
                           int predatorRow,
                           int predatorCol,
                           int preyRow,
                           int preyCol) {
}