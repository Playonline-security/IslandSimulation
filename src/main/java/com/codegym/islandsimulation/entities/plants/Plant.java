package com.codegym.islandsimulation.entities.plants;

import com.codegym.islandsimulation.entities.Organism;

/**
 * Clase abstracta que representa una planta en la simulación.
 * Las plantas pueden ser consumidas por herbívoros y revivir después de un tiempo.
 */
public abstract class Plant extends Organism {
    // Indica si la planta está viva o ha sido consumida
    private boolean isAlive = true;
    
    // Número de ciclos que la planta ha estado muerta
    private int cyclesDead = 0;
    
    // Emoji original de la planta (se restaura al revivir)
    private final String originalEmoji;

    /**
     * Constructor de una planta.
     * @param emoji El emoji representativo de la planta
     * @param name El nombre de la planta (no utilizado actualmente)
     * @param weight El peso de la planta
     */
    public Plant(String emoji, String name, double weight) {
        super(emoji, weight);
        this.originalEmoji = emoji;
    }
    /**
     * Simula que la planta ha sido consumida por un herbívoro.
     * Cambia su estado a no viva y actualiza su emoji.
     */
    public void getEaten() {
        this.isAlive = false;
        this.setEmoji("🌱");
    }

    /**
     * Revive la planta.
     * Restaura su emoji original y la marca como viva.
     */
    public void revive() {
        this.isAlive = true;
        this.cyclesDead = 0;
        this.setEmoji(originalEmoji);
    }

    /**
     * Actualiza el contador de ciclos muertos.
     * Se llama cada ciclo para llevar la cuenta de cuánto tiempo ha estado muerta.
     */
    public void updateCyclesDead() {
        if (!isAlive) {
            this.cyclesDead++;
        }
    }

    /**
     * Verifica si la planta está viva.
     * @return true si la planta está viva, false si ha sido consumida
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Obtiene el número de ciclos que la planta ha estado muerta.
     * @return El número de ciclos muertos
     */
    public int getCyclesDead() {
        return cyclesDead;
    }

    /**
     * Las plantas son plantas.
     * @return true
     */
    @Override
    public boolean isPlant() {
        return true;
    }
}