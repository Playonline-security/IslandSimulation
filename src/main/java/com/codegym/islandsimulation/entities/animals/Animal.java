package com.codegym.islandsimulation.entities.animals;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.interfaces.Carnivore;
import com.codegym.islandsimulation.entities.interfaces.Herbivore;
import com.codegym.islandsimulation.entities.plants.Plant;
import com.codegym.islandsimulation.map.IslandMap;
import com.codegym.islandsimulation.threads.EatingPlantEvent;
import com.codegym.islandsimulation.threads.HuntingEvent;
import com.codegym.islandsimulation.threads.ReproductionEvent;
import com.codegym.islandsimulation.threads.SimulationEventRegistry;
import com.codegym.islandsimulation.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Clase abstracta que representa un animal en la simulación.
 * Define el comportamiento común de todos los animales: movimiento, alimentación y reproducción.
 */
public abstract class Animal extends Organism {
    // Velocidad máxima de movimiento del animal
    private final int maxSpeed;
    
    // Cantidad de comida necesaria para mantener el peso
    private final double foodNeeded;
    
    // Peso inicial del animal (usado como referencia para reproducción)
    private final double initialWeight;
    
    // Nivel actual de comida del animal
    private double currentFoodLevel;
    
    // Pérdida de peso por ciclo cuando el animal no come
    private static final double STARVATION_WEIGHT_LOSS_PER_CYCLE = 0.5;

    // Estados de descanso del animal
    private boolean isRestingAfterHunt = false;
    private boolean isRestingAfterEatingPlant = false;
    private boolean isRestingAfterReproduction = false;

    // Porcentaje mínimo del peso inicial necesario para reproducirse
    private static final double REPRODUCTION_MIN_WEIGHT_PERCENTAGE = 0.6;

    // Tiempo de espera antes de poder reproducirse nuevamente
    private int reproductionCooldown = 0;

    /**
     * Constructor de un animal.
     * @param emoji El emoji representativo del animal
     * @param weight El peso inicial del animal
     * @param maxSpeed La velocidad máxima de movimiento
     * @param foodNeeded La cantidad de comida necesaria por ciclo
     */
    public Animal(String emoji, double weight, int maxSpeed, double foodNeeded) {
        super(emoji, weight);
        this.initialWeight = weight;
        this.maxSpeed = maxSpeed;
        this.foodNeeded = foodNeeded;
        this.currentFoodLevel = foodNeeded;
    }

    /**
     * Obtiene la cantidad de comida necesaria para el animal.
     * @return La cantidad de comida necesaria
     */
    public double getFoodNeeded() {
        return foodNeeded;
    }

    /**
     * Obtiene el peso inicial del animal.
     * @return El peso inicial
     */
    public double getInitialWeight() {
        return initialWeight;
    }

    /**
     * Establece si el animal está descansando después de cazar.
     * @param restingAfterHunt true si está descansando
     */
    public void setRestingAfterHunt(boolean restingAfterHunt) {
        this.isRestingAfterHunt = restingAfterHunt;
    }

    /**
     * Establece el tiempo de espera para reproducción.
     * @param reproductionCooldown El número de ciclos de espera
     */
    public void setReproductionCooldown(int reproductionCooldown) {
        this.reproductionCooldown = reproductionCooldown;
    }

    /**
     * Establece si el animal está descansando después de reproducirse.
     * @param restingAfterReproduction true si está descansando
     */
    public void setRestingAfterReproduction(boolean restingAfterReproduction) {
        this.isRestingAfterReproduction = restingAfterReproduction;
    }

    /**
     * Los animales no son plantas.
     * @return false
     */
    @Override
    public boolean isPlant() {
        return false;
    }

    /**
     * Mueve el animal en el mapa.
     * El animal se mueve aleatoriamente en direcciones válidas hasta agotar su velocidad.
     * @param map El mapa donde se mueve el animal
     * @param currentRow La fila actual del animal
     * @param currentCol La columna actual del animal
     */
    public void move(IslandMap map, int currentRow, int currentCol) {
        // Si el animal está descansando, no se mueve
        if (isRestingAfterEatingPlant || isRestingAfterHunt || isRestingAfterReproduction) {
            this.isRestingAfterEatingPlant = false;
            this.isRestingAfterHunt = false;
            return;
        }

        Random random = new Random();
        // Número de pasos que puede dar el animal (1 + aleatorio hasta maxSpeed)
        int stepsRemaining = 1 + random.nextInt(maxSpeed);
        int row = currentRow;
        int col = currentCol;

        // Remueve el animal de su posición actual
        map.setOrganismAt(currentRow, currentCol, null);

        // Mueve el animal paso a paso
        while (stepsRemaining > 0) {
            // Todas las direcciones posibles (8 direcciones)
            int[][] directions = {
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
            };

            List<int[]> validDirections = new ArrayList<>();

            // Encuentra todas las direcciones válidas (posiciones vacías)
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                if (map.isValidPosition(newRow, newCol) && map.getOrganismAt(newRow, newCol) == null) {
                    validDirections.add(dir);
                }
            }

            // Si no hay direcciones válidas, se detiene
            if (validDirections.isEmpty()) {
                break;
            }

            // Elige una dirección aleatoria válida
            int[] chosenDir = validDirections.get(random.nextInt(validDirections.size()));
            row += chosenDir[0];
            col += chosenDir[1];

            stepsRemaining--;
        }

        // Coloca el animal en su nueva posición
        map.setOrganismAt(row, col, this);
    }

    /**
     * Hace que el animal coma.
     * Reduce el nivel de comida y busca alimento si es necesario.
     * @param map El mapa donde está el animal
     * @param currentRow La fila actual del animal
     * @param currentCol La columna actual del animal
     */
    public void eat(IslandMap map, int currentRow, int currentCol) {
        // Reduce el nivel de comida del animal
        this.currentFoodLevel -= this.getFoodNeeded() * 0.10;
        
        // Si no tiene suficiente comida, pierde peso
        if (this.currentFoodLevel < 0) {
            this.setWeight(this.getWeight() - STARVATION_WEIGHT_LOSS_PER_CYCLE);
        }

        // Si el nivel de comida es bajo, busca alimento
        if (this.currentFoodLevel < this.getFoodNeeded() / 2) {
            List<Organism> adjacentOrganisms = MapUtils.getAdjacentOrganisms(map, currentRow, currentCol);
            Organism potentialFood = null;

            // Busca comida potencial entre organismos adyacentes
            for (Organism organism : adjacentOrganisms) {
                if (organism == null) {
                    continue;
                }

                // Si es carnívoro, busca animales que pueda cazar
                if (this instanceof Carnivore && organism instanceof Animal) {
                    if (((Carnivore) this).getPreyChances().containsKey(organism.getClass())) {
                        potentialFood = organism;
                        break;
                    }
                } 
                // Si es herbívoro, busca plantas vivas
                else if (this instanceof Herbivore && organism.isPlant()) {
                    if (((Plant) organism).isAlive()) {
                        potentialFood = organism;
                        break;
                    }
                }
            }

            // Si encontró comida, intenta consumirla
            if (potentialFood != null) {
                if (this instanceof Carnivore) {
                    // Lógica de caza para carnívoros
                    Map<Class<? extends Organism>, Integer> preyChances = ((Carnivore) this).getPreyChances();
                    int successChance = preyChances.getOrDefault(potentialFood.getClass(), 0);

                    if (new Random().nextInt(100) < successChance) {
                        // Caza exitosa
                        this.currentFoodLevel += potentialFood.getWeight() / 2;
                        int[] preyPos = MapUtils.findOrganismPosition(map, potentialFood);
                        if (preyPos != null) {
                            // Registra el evento de caza
                            SimulationEventRegistry.addHuntingEvent(new HuntingEvent(
                                    this, potentialFood, currentRow, currentCol, preyPos[0], preyPos[1]
                            ));
                        }
                    }
                } else {
                    // Lógica de consumo de plantas para herbívoros
                    this.currentFoodLevel = this.getFoodNeeded();
                    int[] plantPos = MapUtils.findOrganismPosition(map, potentialFood);
                    if (plantPos != null) {
                        // Registra el evento de consumo de planta
                        SimulationEventRegistry.addEatingPlantEvent(new EatingPlantEvent(
                                this, currentRow, currentCol, plantPos[0], plantPos[1]
                        ));
                        this.isRestingAfterEatingPlant = true;
                    }
                }
            }
        }
    }

    /**
     * Hace que el animal intente reproducirse.
     * Busca una pareja del mismo tipo y una posición vacía para la cría.
     * @param map El mapa donde está el animal
     * @param currentRow La fila actual del animal
     * @param currentCol La columna actual del animal
     */
    public void reproduce(IslandMap map, int currentRow, int currentCol) {
        // Verifica si el animal tiene el peso mínimo para reproducirse
        double minWeightForReproduction = this.initialWeight * REPRODUCTION_MIN_WEIGHT_PERCENTAGE;

        if (this.getWeight() < minWeightForReproduction || reproductionCooldown > 0) {
            if (reproductionCooldown > 0) {
                reproductionCooldown--;
            }
            return;
        }

        // Busca organismos adyacentes y una posición vacía
        List<Organism> adjacentOrganisms = MapUtils.getAdjacentOrganisms(map, currentRow, currentCol);
        Animal partner = null;
        int[] emptyPosition = MapUtils.findEmptyAdjacentPosition(map, currentRow, currentCol);

        // Busca una pareja del mismo tipo que no esté en cooldown
        for (Organism organism : adjacentOrganisms) {
            if (organism != null && organism.getClass().equals(this.getClass()) && ((Animal) organism).reproductionCooldown == 0) {
                partner = (Animal) organism;
                break;
            }
        }

        // Si encuentra pareja y posición vacía, registra el evento de reproducción
        if (partner != null && emptyPosition != null) {
            // Solo uno de los dos registra el evento (el de ID menor)
            if (this.getId().compareTo(partner.getId()) < 0) {
                SimulationEventRegistry.addReproductionEvent(
                        new ReproductionEvent(this, partner, emptyPosition[0], emptyPosition[1])
                );
            }
        }
    }
}
