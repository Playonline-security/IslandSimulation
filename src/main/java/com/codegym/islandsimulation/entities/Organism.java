package com.codegym.islandsimulation.entities;

import java.util.UUID;

/**
 * Clase abstracta base para todos los organismos en la simulación.
 * Representa tanto animales como plantas con propiedades comunes.
 */
public abstract class Organism {
    // Identificador único para cada organismo
    private final UUID id = UUID.randomUUID();
    
    // Emoji que representa visualmente al organismo
    private String emoji;
    
    // Peso del organismo (importante para la simulación de alimentación)
    private double weight;

    /**
     * Constructor de un organismo.
     * @param emoji El emoji que representa visualmente al organismo
     * @param weight El peso inicial del organismo
     */
    public Organism(String emoji, double weight) {
        this.emoji = emoji;
        this.weight = weight;
    }

    /**
     * Obtiene el emoji del organismo.
     * @return El emoji representativo
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * Establece el emoji del organismo.
     * @param emoji El nuevo emoji
     */
    protected void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    /**
     * Obtiene el peso actual del organismo.
     * @return El peso del organismo
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Establece el peso del organismo.
     * @param weight El nuevo peso
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Obtiene el identificador único del organismo.
     * @return El UUID del organismo
     */
    public UUID getId() {
        return id;
    }

    /**
     * Método abstracto que determina si el organismo es una planta.
     * @return true si es una planta, false si es un animal
     */
    public abstract boolean isPlant();

    /**
     * Representación en string del organismo (su emoji).
     * @return El emoji del organismo
     */
    @Override
    public String toString() {
        return emoji;
    }
}
