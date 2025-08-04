package com.codegym.islandsimulation.engine;

import com.codegym.islandsimulation.map.IslandWebRenderer;
import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.animals.*;
import com.codegym.islandsimulation.entities.plants.Plant;
import com.codegym.islandsimulation.factory.OrganismFactory;
import com.codegym.islandsimulation.factory.OrganismRegistry;
import com.codegym.islandsimulation.map.IslandMap;
import com.codegym.islandsimulation.threads.*;
import com.codegym.islandsimulation.entities.terrain.*;
import com.codegym.islandsimulation.utils.MapUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Motor principal de la simulación de la isla.
 * Coordina todos los aspectos de la simulación: movimiento, alimentación, reproducción,
 * muerte por inanición, y gestión de eventos.
 */
public class IslandEngine {
    // Contador del ciclo actual de simulación
    private int currentCycle = 0;

    // Mapa de la isla que contiene todos los organismos
    private final IslandMap islandMap;

    // Generador de números aleatorios para decisiones estocásticas
    private final Random random = new Random();

    // Ejecutores para diferentes acciones de los animales
    private final MovementExecutor movementExecutor;
    private final EatingExecutor eatingExecutor;
    private final ReproductionExecutor reproductionExecutor;

    // Nuevos componentes de concurrencia mejorada
    private final SimulationThreadPool threadPool;
    private final SimulationSynchronizer synchronizer;
    private final ThreadMonitor threadMonitor;

    // Constantes para el control de población de orugas
    private static final int CATERPILLAR_MIN_POPULATION = 10;
    private static final int CATERPILLAR_MAX_POPULATION = 20;

    // Mapa que mantiene el conteo de población por tipo de organismo
    private final Map<Class<? extends Organism>, Long> populationCounts = new ConcurrentHashMap<>();

    // Mapa de organismos que están descansando (después de reproducción o caza)
    private final Map<Organism, Integer> restingOrganisms = new ConcurrentHashMap<>();

    // Mapa de organismos muertos y sus posiciones temporales
    private final Map<Organism, OrganismPosition> deadOrganisms = new ConcurrentHashMap<>();

    // Listas de eventos programados para el siguiente ciclo
    private final List<HuntingEvent> scheduledDeaths = new ArrayList<>();
    private final List<EatingPlantEvent> scheduledPlantChanges = new ArrayList<>();

    // Listas de eventos del ciclo anterior para logging
    private final List<HuntingEvent> lastCycleHuntingEvents = new ArrayList<>();
    private final List<EatingPlantEvent> lastCycleEatingPlantEvents = new ArrayList<>();
    private final List<ReproductionEvent> lastCycleReproductionEvents = new ArrayList<>();
    private final List<ReproductionEvent> lastCycleOffspringEvents = new ArrayList<>();

    // Lista de plantas que pueden revivir después de ser consumidas
    private final List<Plant> revivablePlants = Collections.synchronizedList(new ArrayList<>());

    // Constantes para la reproducción
    private static final double REPRODUCTION_WEIGHT_LOSS_FACTOR = 0.2;
    private static final double OFFSPRING_WEIGHT_PERCENTAGE = 0.3;
    private static final int REPRODUCTION_REST_CYCLES = 10;

    // Ciclos necesarios para que una planta reviva
    private static final int PLANT_REVIVAL_CYCLES = 5;

    /**
     * Constructor del motor de simulación.
     * @param rows Número de filas del mapa
     * @param cols Número de columnas del mapa
     */
    public IslandEngine(int rows, int cols) {
        this.islandMap = new IslandMap(rows, cols);
        this.movementExecutor = new MovementExecutor(islandMap);
        this.eatingExecutor = new EatingExecutor(islandMap);
        this.reproductionExecutor = new ReproductionExecutor(islandMap);

        // Inicializar componentes de concurrencia mejorada
        this.threadPool = new SimulationThreadPool();
        this.synchronizer = new SimulationSynchronizer();
        this.threadMonitor = new ThreadMonitor();

        // Iniciar monitoreo de hilos
        this.threadMonitor.startMonitoring();

        populateMap(islandMap);
        initializePopulationCounts();
    }

    public int getCycleNumber() {
        return currentCycle;
    }

    /**
     * Inicializa el conteo de población basado en los organismos existentes en el mapa.
     */
    private void initializePopulationCounts() {
        Map<Class<? extends Organism>, Long> initialCounts = islandMap.getOrganisms().stream()
                .filter(o -> o instanceof Animal)
                .collect(Collectors.groupingBy(Organism::getClass, Collectors.counting()));
        populationCounts.putAll(initialCounts);
    }

    /**
     * Puebla el mapa con organismos iniciales.
     * Coloca terreno, plantas y animales de forma aleatoria.
     * @param map El mapa a poblar
     */
    public void populateMap(IslandMap map) {
        // Tipos de terreno que se colocan en clusters
        List<Class<? extends Organism>> clusterableTerrain = List.of(
                Tree.class,
                PalmTree.class,
                ConiferousTree.class,
                Rock.class,
                Water.class
        );

        // Coloca clusters de terreno
        for (Class<? extends Organism> terrainType : clusterableTerrain) {
            for (int i = 0; i < 2; i++) {
                int row = random.nextInt(map.getRows());
                int col = random.nextInt(map.getCols());
                int clusterSize = 5 + random.nextInt(10);

                placeOrganismCluster(map, terrainType, row, col, clusterSize);
            }
        }

        // Obtiene todos los tipos de organismos disponibles
        List<Class<? extends Organism>> organismTypes = OrganismRegistry.getAllOrganismTypes();
        List<Class<? extends Organism>> animalTypes = organismTypes.stream()
                .filter(c -> !Objects.requireNonNull(OrganismFactory.createOrganism(c)).isPlant())
                .filter(c -> !clusterableTerrain.contains(c))
                .filter(c -> c != Mushroom.class && c != MushroomGrass.class)
                .toList();

        // Encuentra el tipo de hierba para colocarla abundantemente
        Class<? extends Organism> grassType = organismTypes.stream()
                .filter(c -> Objects.requireNonNull(OrganismFactory.createOrganism(c)).isPlant())
                .findFirst().orElse(null);

        // Tipos de terreno que se colocan aleatoriamente
        List<Class<? extends Organism>> randomTerrain = List.of(Mushroom.class, MushroomGrass.class);

        // Coloca terreno aleatorio
        for (Class<? extends Organism> randomType : randomTerrain) {
            int count = 20 + random.nextInt(10);
            for (int i = 0; i < count; i++) {
                int row = random.nextInt(map.getRows());
                int col = random.nextInt(map.getCols());
                if (map.getOrganismAt(row, col) == null) {
                    map.setOrganismAt(row, col, OrganismFactory.createOrganism(randomType));
                }
            }
        }

        // Coloca hierba abundantemente
        if (grassType != null) {
            int grassCount = 100 + random.nextInt(50);
            for (int i = 0; i < grassCount; i++) {
                int row = random.nextInt(map.getRows());
                int col = random.nextInt(map.getCols());
                if (map.getOrganismAt(row, col) == null) {
                    map.setOrganismAt(row, col, OrganismFactory.createOrganism(grassType));
                }
            }
        }

        // Coloca animales
        for (Class<? extends Organism> organismClass : animalTypes) {
            int count = 5 + random.nextInt(5);
            for (int i = 0; i < count; i++) {
                int row = random.nextInt(map.getRows());
                int col = random.nextInt(map.getCols());
                if (map.getOrganismAt(row, col) == null) {
                    map.setOrganismAt(row, col, OrganismFactory.createOrganism(organismClass));
                }
            }
        }

        System.out.println("✅ Isla poblada con éxito.");
    }

    /**
     * Coloca un cluster de organismos del mismo tipo en el mapa.
     * @param map El mapa donde colocar el cluster
     * @param organismClass El tipo de organismo a colocar
     * @param startRow Fila inicial del cluster
     * @param startCol Columna inicial del cluster
     * @param size Tamaño máximo del cluster
     */
    private void placeOrganismCluster(IslandMap map, Class<? extends Organism> organismClass, int startRow, int startCol, int size) {
        if (!map.isValidPosition(startRow, startCol) || map.getOrganismAt(startRow, startCol) != null) {
            return;
        }

        // Cola para el algoritmo de expansión del cluster
        Queue<int[]> positions = new LinkedList<>();
        positions.add(new int[]{startRow, startCol});

        // Coloca el organismo inicial
        try {
            Organism organism = organismClass.getDeclaredConstructor().newInstance();
            map.setOrganismAt(startRow, startCol, organism);
        } catch (Exception e) {
            System.err.println("Error al crear instancia de " + organismClass.getSimpleName());
            return;
        }

        int placedCount = 1;

        // Expande el cluster usando BFS
        while (!positions.isEmpty() && placedCount < size) {
            int[] currentPos = positions.poll();
            int currentRow = currentPos[0];
            int currentCol = currentPos[1];

            // Direcciones: arriba, abajo, izquierda, derecha
            int[][] directions = {
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1}
            };

            for (int[] dir : directions) {
                int newRow = currentRow + dir[0];
                int newCol = currentCol + dir[1];
                if (map.isValidPosition(newRow, newCol) && map.getOrganismAt(newRow, newCol) == null && random.nextDouble() > 0.5) {
                    try {
                        Organism organism = organismClass.getDeclaredConstructor().newInstance();
                        map.setOrganismAt(newRow, newCol, organism);
                    } catch (Exception e) {
                        System.err.println("Error al crear instancia de " + organismClass.getSimpleName());
                        continue;
                    }
                    positions.add(new int[]{newRow, newCol});
                    placedCount++;
                    if (placedCount >= size) break;
                }
            }
        }
    }

    /**
     * Procesa los eventos de caza del ciclo actual.
     * Programa las muertes de presas y marca a los depredadores como descansando.
     */
    private void processHuntingEvents() {
        List<HuntingEvent> currentEvents = SimulationEventRegistry.getHuntingEvents();
        lastCycleHuntingEvents.addAll(currentEvents);

        for (HuntingEvent event : currentEvents) {
            int preyRow = event.preyRow();
            int preyCol = event.preyCol();

            Organism prey = islandMap.getOrganismAt(preyRow, preyCol);
            if (prey != null && prey.equals(event.prey())) {
                scheduledDeaths.add(event);
                if (event.predator() instanceof Animal) {
                    ((Animal) event.predator()).setRestingAfterHunt(true);
                }
            }
        }
    }

    /**
     * Procesa los eventos de consumo de plantas del ciclo actual.
     */
    private void processEatingPlantEvents() {
        List<EatingPlantEvent> currentEvents = SimulationEventRegistry.getEatingPlantEvents();
        lastCycleEatingPlantEvents.addAll(currentEvents);
        scheduledPlantChanges.addAll(currentEvents);
    }

    /**
     * Procesa los eventos de reproducción del ciclo actual.
     */
    private void processReproductionEvents() {
        lastCycleReproductionEvents.addAll(SimulationEventRegistry.getReproductionEvents());
    }

    /**
     * Procesa las muertes por inanición.
     * Los animales con peso <= 0 mueren y se convierten en cadáveres temporales.
     */
    private void processStarvationDeaths() {
        List<Organism> toRemove = new ArrayList<>();
        for (int row = 0; row < islandMap.getRows(); row++) {
            for (int col = 0; col < islandMap.getCols(); col++) {
                Organism organism = islandMap.getOrganismAt(row, col);
                if (organism instanceof Animal && organism.getWeight() <= 0) {
                    toRemove.add(organism);
                }
            }
        }

        for (Organism deadAnimal : toRemove) {
            int[] pos = MapUtils.findOrganismPosition(islandMap, deadAnimal);
            if (pos != null) {
                // Reemplaza el animal muerto con un cadáver temporal
                islandMap.setOrganismAt(pos[0], pos[1], new Organism("☠️", 0) {
                    @Override
                    public boolean isPlant() { return false; }
                });
                deadOrganisms.put(deadAnimal, new OrganismPosition(pos[0], pos[1], 5));
            }
        }
    }

    /**
     * Procesa las alarmas de población y añade nuevos animales si es necesario.
     */
    private void processPopulationAlarms() {
        List<PopulationAlarmEvent> alarms = SimulationEventRegistry.getPopulationAlarmEvents();
        for (PopulationAlarmEvent alarm : alarms) {
            Class<? extends Organism> animalType = alarm.getAnimalType();

            int toAdd;
            if (animalType.equals(Caterpillar.class)) {
                // Lógica especial para orugas
                long currentCaterpillars = populationCounts.getOrDefault(Caterpillar.class, 0L);
                toAdd = (int) (CATERPILLAR_MAX_POPULATION - currentCaterpillars);
            } else {
                toAdd = 4;
            }

            // Añade nuevos animales en posiciones aleatorias vacías
            for (int i = 0; i < toAdd; i++) {
                int row = random.nextInt(islandMap.getRows());
                int col = random.nextInt(islandMap.getCols());

                if (islandMap.getOrganismAt(row, col) == null) {
                    Organism newAnimal = OrganismFactory.createOrganism(animalType);
                    if (newAnimal != null) {
                        islandMap.setOrganismAt(row, col, newAnimal);
                        populationCounts.compute(newAnimal.getClass(), (k, v) -> (v == null) ? 1L : v + 1);
                    }
                }
            }
        }
    }

    /**
     * Verifica si la población de un animal está en niveles críticos y activa una alarma.
     * @param deadOrganism El organismo que murió
     */
    private void checkPopulationAndRaiseAlarm(Organism deadOrganism) {
        if (deadOrganism instanceof Animal) {
            Class<? extends Organism> animalType = deadOrganism.getClass();

            long currentCount = populationCounts.getOrDefault(animalType, 0L);

            if (animalType.equals(Caterpillar.class) && currentCount < CATERPILLAR_MIN_POPULATION) {
                SimulationEventRegistry.addPopulationAlarmEvent(new PopulationAlarmEvent(animalType));
            } else if (!animalType.equals(Caterpillar.class) && currentCount <= 1) {
                SimulationEventRegistry.addPopulationAlarmEvent(new PopulationAlarmEvent(animalType));
            }
        }
    }

    /**
     * Aplica los cambios programados en las plantas (consumo).
     */
    private void applyScheduledPlantChanges() {
        for (EatingPlantEvent event : scheduledPlantChanges) {
            int plantRow = event.plantRow();
            int plantCol = event.plantCol();

            Organism plant = islandMap.getOrganismAt(plantRow, plantCol);
            if (plant instanceof Plant) {
                ((Plant) plant).getEaten();
                revivablePlants.add((Plant) plant);
            }
        }
        scheduledPlantChanges.clear();
    }

    /**
     * Aplica los nacimientos programados de crías.
     */
    private void applyScheduledOffspring() {
        for (ReproductionEvent event : lastCycleReproductionEvents) {
            Animal parent1 = event.parent1();
            Animal parent2 = event.parent2();
            int row = event.offspringRow();
            int col = event.offspringCol();

            if (islandMap.getOrganismAt(row, col) == null) {
                Animal offspring = (Animal) OrganismFactory.createOrganism(parent1.getClass());

                if (offspring != null) {
                    // Configura el peso inicial de la cría
                    double offspringInitialWeight = parent1.getInitialWeight() * OFFSPRING_WEIGHT_PERCENTAGE;
                    offspring.setWeight(offspringInitialWeight);
                    islandMap.setOrganismAt(row, col, offspring);

                    // Actualiza el conteo de población
                    populationCounts.compute(offspring.getClass(), (k, v) -> (v == null) ? 1L : v + 1);

                    // Reduce el peso de los padres
                    parent1.setWeight(parent1.getWeight() * (1 - REPRODUCTION_WEIGHT_LOSS_FACTOR));
                    parent1.setReproductionCooldown(2);
                    parent2.setWeight(parent2.getWeight() * (1 - REPRODUCTION_WEIGHT_LOSS_FACTOR));
                    parent2.setReproductionCooldown(2);

                    // Marca a los padres y la cría como descansando
                    parent1.setRestingAfterReproduction(true);
                    parent2.setRestingAfterReproduction(true);

                    restingOrganisms.put(parent1, REPRODUCTION_REST_CYCLES);
                    restingOrganisms.put(parent2, REPRODUCTION_REST_CYCLES);

                    offspring.setRestingAfterReproduction(true);
                    restingOrganisms.put(offspring, REPRODUCTION_REST_CYCLES);

                    lastCycleOffspringEvents.add(event);
                }
            }
        }
    }

    /**
     * Aplica las muertes programadas por caza.
     */
    private void applyScheduledDeaths() {
        for (HuntingEvent event : scheduledDeaths) {
            int preyRow = event.preyRow();
            int preyCol = event.preyCol();

            Organism prey = islandMap.getOrganismAt(preyRow, preyCol);
            if (prey != null && prey.equals(event.prey())) {
                deadOrganisms.put(prey, new OrganismPosition(preyRow, preyCol, 5));
                // Reemplaza la presa con un cadáver temporal
                islandMap.setOrganismAt(preyRow, preyCol, new Organism("☠️", 0) {
                    @Override
                    public boolean isPlant() { return false; }
                });
            }
        }
        scheduledDeaths.clear();
    }

    /**
     * Revive las plantas que han estado muertas el tiempo suficiente.
     */
    private void revivePlants() {
        List<Plant> plantsToRevive = new ArrayList<>();

        for (Plant plant : revivablePlants) {
            if (!plant.isAlive() && plant.getCyclesDead() >= PLANT_REVIVAL_CYCLES) {
                plant.revive();
                plantsToRevive.add(plant);
            }
        }
        revivablePlants.removeAll(plantsToRevive);
    }

    /**
     * Actualiza el contador de ciclos muertos para las plantas consumidas.
     */
    private void updateConsumedPlants() {
        for (Plant plant : revivablePlants) {
            plant.updateCyclesDead();
        }
    }

    /**
     * Actualiza el estado de los organismos que están descansando.
     */
    private void updateRestingOrganisms() {
        List<Organism> toAwake = new ArrayList<>();

        for (Map.Entry<Organism, Integer> entry : restingOrganisms.entrySet()) {
            Organism organism = entry.getKey();
            Integer remainingCycles = entry.getValue();
            remainingCycles--;
            restingOrganisms.put(organism, remainingCycles);

            if (remainingCycles <= 0) {
                toAwake.add(organism);
            }
        }

        for (Organism organism : toAwake) {
            if (organism instanceof Animal) {
                ((Animal) organism).setRestingAfterReproduction(false);
            }
            restingOrganisms.remove(organism);
        }
    }

    /**
     * Actualiza el estado de los organismos muertos.
     * Los cadáveres desaparecen después de un tiempo.
     */
    private void updateDeadOrganisms() {
        List<Organism> toRemoveFromMap = new ArrayList<>();

        for (Map.Entry<Organism, OrganismPosition> entry : deadOrganisms.entrySet()) {
            Organism deadOrganism = entry.getKey();
            OrganismPosition pos = entry.getValue();
            pos.decrementCycles();

            if (pos.getRemainingCycles() <= 0) {
                toRemoveFromMap.add(deadOrganism);
            }
        }

        for (Organism originalOrganism : toRemoveFromMap) {
            OrganismPosition pos = deadOrganisms.get(originalOrganism);
            if (pos != null) {
                islandMap.setOrganismAt(pos.row, pos.col, null);

                if (originalOrganism instanceof Animal) {
                    populationCounts.computeIfPresent(originalOrganism.getClass(), (k, v) -> v > 0 ? v - 1 : 0L);
                    checkPopulationAndRaiseAlarm(originalOrganism);
                }
            }
            deadOrganisms.remove(originalOrganism);
        }
    }

    /**
     * Ejecuta un ciclo completo de simulación.
     * Coordina todos los procesos: movimiento, alimentación, reproducción, etc.
     */
    public void updateSimulationCycle() {
        currentCycle++;
        processPopulationAlarms();
        SimulationEventRegistry.clearPopulationAlarmEvents();

        processStarvationDeaths();

        lastCycleOffspringEvents.clear();

        applyScheduledDeaths();
        applyScheduledPlantChanges();
        applyScheduledOffspring();

        lastCycleHuntingEvents.clear();
        lastCycleEatingPlantEvents.clear();
        lastCycleReproductionEvents.clear();

        SimulationEventRegistry.clearHuntingEvents();
        SimulationEventRegistry.clearEatingPlantEvents();
        SimulationEventRegistry.clearReproductionEvents();

        // Ejecuta las acciones de los animales
        movementExecutor.processAnimalMovements();
        eatingExecutor.processAnimalEating();
        reproductionExecutor.processAnimalReproduction();

        // Procesa los eventos generados
        processHuntingEvents();
        processEatingPlantEvents();
        processReproductionEvents();

        updateRestingOrganisms();

        updateDeadOrganisms();
        updateConsumedPlants();
        revivePlants();

        // Actualiza el conteo de población
        populationCounts.clear();
        islandMap.getOrganisms().stream()
                .filter(o -> o instanceof Animal)
                .collect(Collectors.groupingBy(Organism::getClass, Collectors.counting()))
                .forEach(populationCounts::put);

        String consolidatedLog = generateConsolidatedLog();

        // --- CAMBIO AQUÍ: Se crea el renderizador sin el objeto server ---
        IslandWebRenderer renderer = new IslandWebRenderer(islandMap, populationCounts, currentCycle, consolidatedLog);
        renderer.setHuntingEvents(lastCycleHuntingEvents);
        renderer.setEatingPlantEvents(lastCycleEatingPlantEvents);
        renderer.setReproductionEvents(lastCycleReproductionEvents);
        renderer.setOffspringEvents(lastCycleOffspringEvents);

        // Ejecutar generación de HTML en pool de I/O separado
        threadPool.submitIOTask(() -> {
            try {
                renderer.generateHtmlFile();
                return "HTML generado exitosamente";
            } catch (IOException e) {
                System.err.println("Error generando HTML: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });

        // Imprimir estadísticas de concurrencia cada 10 ciclos
        if (currentCycle % 10 == 0) {
            System.out.println("📈 " + threadPool.getPoolStatistics());
            System.out.println("🔒 " + synchronizer.getLockInfo());
            System.out.println("🧵 " + threadMonitor.getThreadSummary());
        }
    }

    /**
     * Genera un log consolidado de los eventos del ciclo actual.
     * @return String con el resumen de eventos del ciclo
     */
    private String generateConsolidatedLog() {
        StringBuilder log = new StringBuilder();
        log.append("------------- Ciclo ").append(currentCycle).append(" -------------\n\n");

        // Cuenta los nacimientos por especie
        Map<Class<? extends Organism>, Long> births = lastCycleReproductionEvents.stream()
                .collect(Collectors.groupingBy(e -> e.parent1().getClass(), Collectors.counting()));

        long totalBirths = lastCycleReproductionEvents.size();
        long totalHuntingDeaths = lastCycleHuntingEvents.size();
        long totalStarvationDeaths = deadOrganisms.entrySet().stream()
                .filter(e -> !lastCycleHuntingEvents.stream()
                        .anyMatch(hunt -> hunt.prey().getId().equals(e.getKey().getId())))
                .count();

        long totalPlantEaten = lastCycleEatingPlantEvents.size();

        // Registra nacimientos
        if (totalBirths > 0) {
            log.append("✔ Nacimientos:\n");
            births.forEach((species, count) ->
                    log.append("  - ").append(count).append(" nuevos ").append(species.getSimpleName()).append("\n"));
            log.append("\n");
        }

        // Registra muertes
        if (totalHuntingDeaths > 0 || totalStarvationDeaths > 0) {
            log.append("✔ Muertes:\n");
            if (totalHuntingDeaths > 0) {
                log.append("  - ").append(totalHuntingDeaths).append(" animales murieron por caza.\n");
            }
            if (totalStarvationDeaths > 0) {
                log.append("  - ").append(totalStarvationDeaths).append(" animales murieron de hambre.\n");
            }
            log.append("\n");
        }

        // Registra interacciones con plantas
        if (totalPlantEaten > 0) {
            log.append("✔ Interacciones:\n");
            log.append("  - ").append(totalPlantEaten).append(" plantas fueron consumidas.\n");
            log.append("\n");
        }

        // Registra alarmas de población
        if (SimulationEventRegistry.getPopulationAlarmEvents().size() > 0) {
            log.append("✔ Repoblación:\n");
            SimulationEventRegistry.getPopulationAlarmEvents().forEach(alarm -> {
                log.append("  - Alarma para ").append(alarm.getAnimalType().getSimpleName()).append(" activada.\n");
            });
            log.append("\n");
        }

        return log.toString();
    }

    /**
     * Cierra los pools de hilos de forma ordenada al finalizar la simulación.
     */
    public void shutdown() {
        threadPool.shutdown();
        threadMonitor.stopMonitoring();
    }

    private static class OrganismPosition {
        int row;
        int col;
        int remainingCycles;

        public OrganismPosition(int row, int col, int remainingCycles) {
            this.row = row;
            this.col = col;
            this.remainingCycles = remainingCycles;
        }

        public int getRemainingCycles() {
            return remainingCycles;
        }

        public void decrementCycles() {
            remainingCycles--;
        }
    }
}