package com.codegym.islandsimulation.threads;

import com.codegym.islandsimulation.entities.animals.Animal;

/**
 * Registro que representa un evento de reproducción en la simulación.
 * Contiene información sobre los padres y la posición donde nacerá la cría.
 * @param parent1 El primer animal padre
 * @param parent2 El segundo animal padre
 * @param offspringRow Fila donde nacerá la cría
 * @param offspringCol Columna donde nacerá la cría
 */
public record ReproductionEvent(Animal parent1, Animal parent2, int offspringRow, int offspringCol) {
}