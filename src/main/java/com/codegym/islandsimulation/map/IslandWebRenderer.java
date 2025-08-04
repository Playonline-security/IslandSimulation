package com.codegym.islandsimulation.map;

import com.codegym.islandsimulation.entities.Organism;
import com.codegym.islandsimulation.entities.animals.Animal;
import com.codegym.islandsimulation.threads.EatingPlantEvent;
import com.codegym.islandsimulation.threads.HuntingEvent;
import com.codegym.islandsimulation.threads.ReproductionEvent;
import com.codegym.islandsimulation.utils.MapUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Esta clase es responsable de generar una representación visual
 * de la simulación de la isla en un archivo HTML.
 * Crea un archivo `island.html` que muestra el estado de la isla,
 * las estadísticas del ciclo actual y un registro de eventos.
 * La visualización incluye el mapa con emojis, un panel de estadísticas
 * con recuentos de población y eventos, y un registro de texto de lo
 * que ocurrió en el ciclo.
 */

public class IslandWebRenderer {
    // Campos finales para el estado de la simulación
    private final IslandMap island;
    private final Map<Class<? extends Organism>, Long> populationCounts;
    private final int cycleNumber;
    private final String consolidatedLog;  // Este campo no se usa en el HTML, pero se mantiene para compatibilidad
    private final RendererMessages messages;

    // Listas para almacenar los eventos del último ciclo para su renderizado
    private List<HuntingEvent> lastCycleHuntingEvents;
    private List<EatingPlantEvent> lastCycleEatingPlantEvents;
    private List<ReproductionEvent> lastCycleReproductionEvents;
    private List<ReproductionEvent> lastCycleOffspringEvents;

    // Un registro inmutable para representar una posición en el mapa.
    private record Position(int row, int col) {
    }
    /**
     * Constructor que inicializa el renderizador con los datos de la isla,
     * los recuentos de población, el número de ciclo y el log consolidado.
     * El idioma por defecto es español ("es").
     */
    public IslandWebRenderer(IslandMap island, Map<Class<? extends Organism>, Long> populationCounts, int cycleNumber, String consolidatedLog) {
        this(island, populationCounts, cycleNumber, consolidatedLog, "es"); // Español por defecto
    }

    public IslandWebRenderer(IslandMap island, Map<Class<? extends Organism>, Long> populationCounts, int cycleNumber, String consolidatedLog, String language) {
        this.island = island;
        this.populationCounts = populationCounts;
        this.cycleNumber = cycleNumber;
        this.consolidatedLog = consolidatedLog;
        this.messages = new RendererMessages(language);
    }
    /**
     * Calcula el porcentaje de ocupación del mapa de la isla.
     * Se considera ocupado un espacio si contiene un organismo que no es agua ni muerte.
     * Retorna un valor entre 0.0 y 100.0.
     */
    private double getOccupationPercent() {
        int totalCells = island.getRows() * island.getCols();
        long occupiedCells = island.getOrganisms().stream()
                .filter(o -> o != null && !o.getEmoji().equals("💧") && !o.getEmoji().equals("☠️"))
                .count();
        return totalCells == 0 ? 0.0 : (occupiedCells * 100.0) / totalCells;
    }

    public void setHuntingEvents(List<HuntingEvent> events) {
        this.lastCycleHuntingEvents = events;
    }

    public void setEatingPlantEvents(List<EatingPlantEvent> events) {
        this.lastCycleEatingPlantEvents = events;
    }

    public void setReproductionEvents(List<ReproductionEvent> events) {
        this.lastCycleReproductionEvents = events;
    }

    public void setOffspringEvents(List<ReproductionEvent> events) {
        this.lastCycleOffspringEvents = events;
    }
    /**
     * Genera un archivo HTML que representa el estado actual de la isla,
     * incluyendo el mapa, estadísticas y un registro de eventos.
     * El archivo se guarda como `island.html` en el directorio actual.
     *
     * @throws IOException si ocurre un error al escribir el archivo.
     */
    public void generateHtmlFile() throws IOException {
        StringBuilder html = new StringBuilder();

        Set<Position> huntingPositions = createHuntingPositionsSet();
        Set<Position> eatingPlantPositions = createEatingPlantPositionsSet();
        Set<Position> reproductionPositions = createReproductionPositionsSet();
        Set<Position> offspringPositions = createOffspringPositionsSet();

        html.append("<!DOCTYPE html>\n<html>\n<head>\n")
                .append("<meta charset='UTF-8'>\n")
                .append("<title>").append(messages.get("title")).append("</title>\n")
                .append("<style>\n")

                .append("body {\n")
                .append("  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif, 'Segoe UI Emoji', 'Noto Color Emoji';\n")
                .append("  margin: 0;\n")
                .append("  padding: 0;\n")
                .append("  background-color: #f9f9f9;\n")
                .append("}\n")

                .append(".main-container {\n")
                .append("  display: flex;\n")
                .append("  flex-direction: column;\n")
                .append("  align-items: center;\n")
                .append("  gap: 18px;\n")
                .append("  min-height: 100vh;\n")
                .append("  padding: 20px;\n")
                .append("  box-sizing: border-box;\n")
                .append("}\n")

                .append(".map-and-side-panel-container {\n")
                .append("  display: flex;\n")
                .append("  flex-direction: row;\n")
                .append("  justify-content: center;\n")
                .append("  align-items: flex-start;\n")
                .append("  gap: 24px;\n")
                .append("}\n")

                .append(".side-panel {\n")
                .append("  display: flex;\n")
                .append("  flex-direction: column;\n")
                .append("  gap: 18px;\n")
                .append("  width: 300px;\n")
                .append("}\n")

                .append(".map-panel-container {\n")
                .append("  display: flex;\n")
                .append("  flex-direction: column;\n")
                .append("  align-items: center;\n")
                .append("  gap: 18px;\n")
                .append("}\n")

                .append(".grid-container {\n")
                .append("  background: #fff;\n")
                .append("  border-radius: 10px;\n")
                .append("  box-shadow: 0 1px 8px rgba(0,0,0,0.04);\n")
                .append("  padding: 12px;\n")
                .append("}\n")

                .append(".grid {\n")
                .append("  display: grid;\n")
                .append("  grid-template-columns: repeat(").append(island.getCols()).append(", 26px);\n")
                .append("  gap: 1px;\n")
                .append("}\n")

                .append(".cell {\n")
                .append("  width: 24px;\n")
                .append("  height: 24px;\n")
                .append("  display: flex;\n")
                .append("  align-items: center;\n")
                .append("  justify-content: center;\n")
                .append("  font-size: 20px;\n")
                .append("  background: rgba(243, 246, 248, 0.9);\n")
                .append("  border: 1px solid #e0e3e7;\n")
                .append("  border-radius: 3px;\n")
                .append("  transition: box-shadow 0.15s;\n")
                .append("  position: relative;\n")
                .append("}\n")

                .append(".cell:hover {\n")
                .append("  box-shadow: 0 0 4px #b0b0b0;\n")
                .append("  z-index: 2;\n")
                .append("}\n")

                .append(".hunting-cell {\n")
                .append("  background: #ffeaea;\n")
                .append("  border-color: #ffb3b3;\n")
                .append("  animation: pulse 0.8s ease-out;\n")
                .append("}\n")

                .append(".eatingPlant {\n")
                .append("  background: #eaffea;\n")
                .append("  border-color: #b3ffb3;\n")
                .append("  animation: pop 0.6s ease;\n")
                .append("}\n")

                .append(".reproduction-cell {\n")
                .append("  background: #eaf4ff;\n")
                .append("  border-color: #b3e0ff;\n")
                .append("  animation: pop 0.6s ease;\n")
                .append("}\n")

                .append(".offspring-cell {\n")
                .append("  background: #fffbe6;\n")
                .append("  border-color: #ffe680;\n")
                .append("  animation: pop 0.6s ease;\n")
                .append("}\n")

                .append(".death-cell {\n")
                .append("  background: #f7f7f7;\n")
                .append("  border-style: dashed;\n")
                .append("  border-color: #cccccc;\n")
                .append("  animation: pop 0.6s ease;\n")
                .append("}\n")

                .append(".water-cell {\n")
                .append("  background: #e3f0ff;\n")
                .append("  border-color: #b3d8ff;\n")
                .append("}\n")

                .append(".cell[data-tooltip]:hover:after {\n")
                .append("  content: attr(data-tooltip);\n")
                .append("  position: absolute;\n")
                .append("  bottom: 110%;\n")
                .append("  left: 50%;\n")
                .append("  transform: translateX(-50%);\n")
                .append("  background: #222;\n")
                .append("  color: #fff;\n")
                .append("  padding: 2px 8px;\n")
                .append("  border-radius: 4px;\n")
                .append("  font-size: 11px;\n")
                .append("  white-space: nowrap;\n")
                .append("  pointer-events: none;\n")
                .append("  z-index: 10;\n")
                .append("}\n")

                .append(".stats, .log-panel {\n")
                .append("  background: #fff;\n")
                .append("  border-radius: 8px;\n")
                .append("  box-shadow: 0 1px 8px rgba(0,0,0,0.04);\n")
                .append("  padding: 12px 18px;\n")
                .append("  font-size: 13px;\n")
                .append("}\n")

                .append(".stats {\n")
                .append("  min-width: 240px;\n")
                .append("  background: rgba(255, 255, 255, 0.85);\n")
                .append("  padding: 15px;\n")
                .append("  border-radius: 8px;\n")
                .append("  box-shadow: 0 2px 6px rgba(0,0,0,0.1);\n")
                .append("  font-size: 14px;\n")
                .append("}\n")

                .append(".log-panel {\n")
                .append("  min-width: 240px;\n")
                .append("}\n")

                .append(".log-content {\n")
                .append("  background: #f8f9fa;\n")
                .append("  border: 1px solid #e0e3e7;\n")
                .append("  border-radius: 8px;\n")
                .append("  padding: 8px;\n")
                .append("}\n")

                .append(".log-entry {\n")
                .append("  display: flex;\n")
                .append("  align-items: flex-start;\n")
                .append("  gap: 8px;\n")
                .append("  padding: 6px;\n")
                .append("  border-radius: 4px;\n")
                .append("  margin-bottom: 4px;\n")
                .append("  font-size: 13px;\n")
                .append("  line-height: 1.4;\n")
                .append("  white-space: normal;\n")
                .append("  word-wrap: break-word;\n")
                .append("}\n")

                .append(".log-entry:last-child {\n")
                .append("  margin-bottom: 0;\n")
                .append("}\n")

                .append(".log-icon {\n")
                .append("  font-size: 16px;\n")
                .append("  flex-shrink: 0;\n")
                .append("}\n")

                .append(".log-text {\n")
                .append("  color: #333;\n")
                .append("}\n")

                .append(".log-hunting {\n")
                .append("  background-color: #ffe6e6;\n")
                .append("}\n")

                .append(".log-eating {\n")
                .append("  background-color: #e6ffe6;\n")
                .append("}\n")

                .append(".log-reproduction {\n")
                .append("  background-color: #e6f7ff;\n")
                .append("}\n")

                .append(".log-offspring {\n")
                .append("  background-color: #fffbe6;\n")
                .append("}\n")

                .append(".log-death {\n")
                .append("  background-color: #f2f2f2;\n")
                .append("  color: #666;\n")
                .append("}\n")

                .append(".stats h3, .stats h4 {\n")
                .append("  margin: 0 0 12px 0;\n")
                .append("  color: #333;\n")
                .append("}\n")
                .append(".divider {\n")
                .append("  border: 0;\n")
                .append("  height: 1px;\n")
                .append("  background: #e0e0e0;\n")
                .append("  margin: 15px 0;\n")
                .append("}\n")
                .append(".stat-row {\n")
                .append("  display: flex;\n")
                .append("  justify-content: space-between;\n")
                .append("  padding: 4px 0;\n")
                .append("}\n")
                .append(".stat-row span:first-child {\n")
                .append("  color: #555;\n")
                .append("}\n")
                .append(".stat-row span:last-child {\n")
                .append("  font-weight: bold;\n")
                .append("  color: #000;\n")
                .append("}\n")
                .append(".event-list {\n")
                .append("  list-style-type: none;\n")
                .append("  padding: 0;\n")
                .append("  margin: 0;\n")
                .append("}\n")
                .append(".event-list li {\n")
                .append("  display: flex;\n")
                .append("  justify-content: space-between;\n")
                .append("  padding: 4px 0;\n")
                .append("  color: #555;\n")
                .append("}\n")
                .append(".event-list li span {\n")
                .append("  font-weight: bold;\n")
                .append("  color: #000;\n")
                .append("}\n")

                .append("@media (max-width: 900px) {\n")
                .append("  .map-and-side-panel-container {\n")
                .append("    flex-direction: column;\n")
                .append("    align-items: center;\n")
                .append("  }\n")
                .append("  .side-panel {\n")
                .append("    width: 100%;\n")
                .append("  }\n")
                .append("  .stats {\n")
                .append("    min-width: unset;\n")
                .append("    width: 100%;\n")
                .append("  }\n")
                .append("  .log-panel {\n")
                .append("    min-width: unset;\n")
                .append("    width: 100%;\n")
                .append("  }\n")
                .append("}\n")

                .append("@keyframes pop {\n")
                .append("  0% { transform: scale(0.9); opacity: 0.8; }\n")
                .append("  50% { transform: scale(1.1); opacity: 1; }\n")
                .append("  100% { transform: scale(1); }\n")
                .append("}\n")

                .append("@keyframes pulse {\n")
                .append("  0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(255, 0, 0, 0.4); }\n")
                .append("  100% { transform: scale(1.05); box-shadow: 0 0 0 8px rgba(255, 0, 0, 0); }\n")
                .append("}\n")

                .append(".species-row {\n")
                .append("  display: flex;\n")
                .append("  flex-wrap: wrap;\n")
                .append("  justify-content: center;\n")
                .append("  gap: 12px;\n")
                .append("  font-size: 18px;\n")
                .append("  align-items: center;\n")
                .append("  margin-top: 12px;\n")
                .append("}\n")

                .append(".species-item {\n")
                .append("  display: inline-flex;\n")
                .append("  align-items: center;\n")
                .append("  justify-content: center;\n")
                .append("  padding: 4px 8px;\n")
                .append("  border-radius: 6px;\n")
                .append("  font-weight: bold;\n")
                .append("  min-height: 32px;\n")
                .append("  color: #333;\n")
                .append("  transition: background-color 0.2s ease;\n")
                .append("}\n")

                .append(".species-item.status-red {\n")
                .append("  background-color: #ffcccc;\n")
                .append("  border: none;\n")
                .append("}\n")

                .append(".species-item.status-yellow {\n")
                .append("  background-color: #fff2cc;\n")
                .append("  border: none;\n")
                .append("}\n")

                .append(".species-item.status-green {\n")
                .append("  background-color: #ccffcc;\n")
                .append("  border: none;\n")
                .append("}\n")

                .append("</style>\n")

                .append("<script>\n")
                .append("document.addEventListener('DOMContentLoaded', () => {\n")
                .append("  const logContainer = document.getElementById('event-log-container');\n")
                .append("  if (logContainer) {\n")
                .append("    logContainer.innerHTML = `").append(generateLogHtml().replace("`", "\\`")).append("`;\n")
                .append("  }\n")
                .append("  setTimeout(() => location.reload(), 3000);\n")
                .append("});\n")
                .append("</script>\n")

                .append("</head>\n<body>\n")

                .append("<h2 style='text-align: center;'>").append(messages.get("mapTitle")).append("</h2>\n")

                .append("<div class='main-container'>\n")

                .append("<div class='map-and-side-panel-container'>\n")

                .append("<div class='map-panel-container'>\n")

                .append("<div class='grid-container'>\n<div class='grid'>\n");

// --- Renderizado del mapa ---
        for (int row = 0; row < island.getRows(); row++) {
            for (int col = 0; col < island.getCols(); col++) {
                Organism organism = island.getOrganismAt(row, col);
                String emoji = (organism == null) ? " " : organism.getEmoji();
                String cellStyle = "cell";
                String tooltip = "";

                Position currentPos = new Position(row, col);
                if (offspringPositions.contains(currentPos)) {
                    cellStyle += " offspring-cell";
                    tooltip = "Nacimiento";
                } else if (reproductionPositions.contains(currentPos)) {
                    cellStyle += " reproduction-cell";
                    tooltip = "Reproducción";
                } else if (huntingPositions.contains(currentPos)) {
                    cellStyle += " hunting-cell";
                    tooltip = "Caza";
                } else if (eatingPlantPositions.contains(currentPos)) {
                    cellStyle += " eatingPlant";
                    tooltip = "Comiendo planta";
                } else if ("💧".equals(emoji)) {
                    cellStyle += " water-cell";
                    tooltip = "Agua";
                } else if ("☠️".equals(emoji)) {
                    cellStyle += " death-cell";
                    tooltip = "Muerte";
                }

                html.append("<div class='").append(cellStyle).append("'");
                if (!tooltip.isEmpty()) {
                    html.append(" data-tooltip='").append(tooltip).append("'");
                }
                html.append(">").append(emoji).append("</div>\n");
            }
        }

        html.append("</div></div>\n"); // Cierre de grid y grid-container

// --- Contenedor para el conteo de animales ---
        html.append("<div class='species-row'>\n");

        List<Class<? extends Organism>> sortedSpecies = new ArrayList<>(populationCounts.keySet());
        sortedSpecies.sort(Comparator.comparing(Class::getSimpleName));

        for (Class<? extends Organism> species : sortedSpecies) {
            long count = populationCounts.getOrDefault(species, 0L);
            String emoji = MapUtils.getEmojiForClass(species);
            String colorClass = getStatusClass(species, count);

            html.append("<div class='species-item ").append(colorClass).append("'>")
                    .append(emoji).append(" ").append(count)
                    .append("</div>\n");
        }

        html.append("</div>\n"); // Cierre de species-row

        html.append("</div>\n"); // Cierre de map-panel-container

        // Contenedor para el panel de estadísticas y el log
        html.append("<div class='side-panel'>\n");

// --- Panel de estadísticas ---
        long deathsThisCycle = island.getOrganisms().stream().filter(o -> o.getEmoji().equals("☠️")).count();
        int huntsThisCycle = lastCycleHuntingEvents != null ? lastCycleHuntingEvents.size() : 0;
        int reproductionsThisCycle = lastCycleReproductionEvents != null ? lastCycleReproductionEvents.size() : 0;
        int foodEatenThisCycle = lastCycleEatingPlantEvents != null ? lastCycleEatingPlantEvents.size() : 0;
        double occupationPercent = getOccupationPercent();

        html.append("<div class='stats'>\n")
                .append("<h3>").append(messages.get("stats")).append("</h3>\n")
                .append("<div class='stat-row'><span>").append("📅 Ciclo:").append("</span><span>").append(this.cycleNumber).append("</span></div>\n")
                .append("<div class='stat-row'><span>").append("👥 Población Total:").append("</span><span>").append(calculateTotalPopulation()).append("</span></div>\n")
                .append("<div class='stat-row'><span>").append("🗺️ Ocupación del mapa:").append("</span><span>").append(String.format("%.1f", occupationPercent)).append("%</span></div>\n")

                .append("<hr class='divider'>\n")

                .append("<h4>").append("Eventos del ciclo").append("</h4>\n")
                .append("<ul class='event-list'>\n")
                .append("<li>").append("❤️ Reproducciones:").append(" <span>").append(reproductionsThisCycle).append("</span></li>\n")
                .append("<li>").append("💀 Animales muertos:").append(" <span>").append(deathsThisCycle).append("</span></li>\n")
                .append("<li>").append("⚔️ Acciones de Caza:").append(" <span>").append(huntsThisCycle).append("</span></li>\n")
                .append("<li>").append("🌿 Plantas consumidas:").append(" <span>").append(foodEatenThisCycle).append("</span></li>\n")
                .append("</ul>\n")
                .append("</div>\n"); // stats

// --- Panel de eventos (Log consolidado) ---
        html.append("<div class='log-panel'>\n")
                .append("<div class='log-content' id='event-log-container'></div>\n")
                .append("</div>\n");

        html.append("</div>\n"); // Cierre de side-panel
        html.append("</div>\n"); // Cierre de map-and-side-panel-container

        html.append("</div>\n"); // main-container

        html.append("</body>\n</html>");

        try (FileWriter fw = new FileWriter("island.html")) {
            fw.write(html.toString());
        }
    }
    /**
     * Calcula la población total de organismos que son instancias de Animal.
     * Retorna la suma de todos los recuentos de población para las clases que heredan de Animal.
     */
    private long calculateTotalPopulation() {
        return this.populationCounts.entrySet().stream()
                .filter(entry -> Animal.class.isAssignableFrom(entry.getKey()))
                .mapToLong(Map.Entry::getValue)
                .sum();
    }

    private String getStatusClass(Class<? extends Organism> species, long count) {
        // Reglas para la mayoría de los animales
        if (Animal.class.isAssignableFrom(species)) {
            if (count >= 1 && count <= 3) {
                return "status-red";
            } else if (count >= 4 && count <= 6) {
                return "status-yellow";
            } else {
                return "status-green";
            }
        }

        // Si no es un animal, por defecto es estable
        return "status-green";
    }

    private String generateLogHtml() {
        StringBuilder logHtml = new StringBuilder();

        // 1. Mensajes de estado de especies (ej. "Queda solo 1 animal...")
        generateSpeciesStatusMessages(logHtml);

        // 2. Mensajes de eventos del ciclo (caza, reproducción, etc.)
        generateCycleEventMessages(logHtml);

        // Si no hay eventos, muestra un mensaje por defecto
        if (logHtml.isEmpty()) {
            return "<p style='text-align: center; color: #888;'>No hay eventos o cambios significativos en este ciclo.</p>";
        }

        return logHtml.toString();
    }
    /**
     * Genera mensajes de estado para las especies en peligro o con poblaciones críticas.
     * Agrega mensajes al registro HTML según las reglas definidas.
     */
    private void generateSpeciesStatusMessages(StringBuilder logHtml) {
        List<Class<? extends Organism>> sortedSpecies = new ArrayList<>(populationCounts.keySet());
        sortedSpecies.sort(Comparator.comparing(Class::getSimpleName));

        for (Class<? extends Organism> species : sortedSpecies) {
            long count = populationCounts.getOrDefault(species, 0L);
            String emoji = MapUtils.getEmojiForClass(species);
            String speciesName = species.getSimpleName();

            if (Animal.class.isAssignableFrom(species)) {
                if (count == 1) {
                    addLogEntry(logHtml, "⚠️", "log-warning", "Solo queda un " + speciesName + " " + emoji + " en la isla.");
                } else if (count == 0) {
                    addLogEntry(logHtml, "💔", "log-extinction", "La especie " + speciesName + " " + emoji + " se ha extinguido.");
                } else if (count >= 1 && count <= 3) {
                    addLogEntry(logHtml, "🚨", "log-warning", "La población de " + speciesName + " " + emoji + " es muy baja (" + count + ").");
                }
            }
        }
    }
    /**
     * Genera mensajes de eventos del ciclo actual, como caza, reproducción, nacimiento y consumo de plantas.
     * Agrega estos mensajes al registro HTML.
     */
    private void generateCycleEventMessages(StringBuilder logHtml) {
        if (lastCycleHuntingEvents != null && !lastCycleHuntingEvents.isEmpty()) {
            for (HuntingEvent event : lastCycleHuntingEvents) {
                // predator y prey vienen como objetos en el HuntingEvent
                String predatorEmoji = MapUtils.getEmojiForClass(event.predator().getClass());
                String preyEmoji = MapUtils.getEmojiForClass(event.prey().getClass());
                String message = predatorEmoji + " " + event.predator().getClass().getSimpleName() + " mató a " + preyEmoji + " " + event.prey().getClass().getSimpleName() + ".";
                addLogEntry(logHtml, "⚔️", "log-hunting", message);
            }
        }

        if (lastCycleReproductionEvents != null && !lastCycleReproductionEvents.isEmpty()) {
            for (ReproductionEvent event : lastCycleReproductionEvents) {
                // parent1 y parent2 vienen como objetos en el ReproductionEvent
                String parent1Emoji = MapUtils.getEmojiForClass(event.parent1().getClass());
                String parent2Emoji = MapUtils.getEmojiForClass(event.parent2().getClass());
                String message = "Un " + event.parent1().getClass().getSimpleName() + " " + parent1Emoji + " y un " + event.parent2().getClass().getSimpleName() + " " + parent2Emoji + " se reprodujeron.";
                addLogEntry(logHtml, "❤️", "log-reproduction", message);
            }
        }

        if (lastCycleOffspringEvents != null && !lastCycleOffspringEvents.isEmpty()) {
            for (ReproductionEvent event : lastCycleOffspringEvents) {
                // El descendiente se busca en el mapa usando las coordenadas
                Organism offspring = island.getOrganismAt(event.offspringRow(), event.offspringCol());
                if (offspring != null) {
                    String offspringEmoji = MapUtils.getEmojiForClass(offspring.getClass());
                    String message = "Nació un nuevo " + offspring.getClass().getSimpleName() + " " + offspringEmoji + ".";
                    addLogEntry(logHtml, "🐣", "log-offspring", message);
                }
            }
        }

        if (lastCycleEatingPlantEvents != null && !lastCycleEatingPlantEvents.isEmpty()) {
            for (EatingPlantEvent event : lastCycleEatingPlantEvents) {
                // herbivore viene como objeto en el EatingPlantEvent
                String herbivoreEmoji = MapUtils.getEmojiForClass(event.herbivore().getClass());

                // La planta debe ser buscada en el mapa usando las coordenadas del evento
                Organism plant = island.getOrganismAt(event.plantRow(), event.plantCol());
                if (plant != null) {
                    String plantEmoji = MapUtils.getEmojiForClass(plant.getClass());
                    String message = "Un " + event.herbivore().getClass().getSimpleName() + " " + herbivoreEmoji + " se comió un(a) " + plant.getClass().getSimpleName() + " " + plantEmoji + ".";
                    addLogEntry(logHtml, "🌿", "log-eating", message);
                }
            }
        }
    }
    /**
     * Agrega una entrada al registro HTML con un ícono, clase de color y mensaje.
     * Utiliza un formato consistente para las entradas del registro.
     *
     * @param logHtml   El StringBuilder donde se agregará la entrada del registro.
     * @param icon      El ícono que se mostrará en la entrada del registro.
     * @param colorClass La clase de color para el estilo de la entrada.
     * @param message   El mensaje de texto que describe el evento.
     */
    private void addLogEntry(StringBuilder logHtml, String icon, String colorClass, String message) {
        logHtml.append("<div class='log-entry ").append(colorClass).append("'>")
                .append("<span class='log-icon'>").append(icon).append("</span> ")
                .append("<span class='log-text'>").append(message).append("</span>")
                .append("</div>");
    }

    private Set<Position> createHuntingPositionsSet() {
        Set<Position> positions = new HashSet<>();
        if (lastCycleHuntingEvents != null) {
            for (HuntingEvent event : lastCycleHuntingEvents) {
                positions.add(new Position(event.predatorRow(), event.predatorCol()));
                positions.add(new Position(event.preyRow(), event.preyCol()));
            }
        }
        return positions;
    }

    private Set<Position> createEatingPlantPositionsSet() {
        Set<Position> positions = new HashSet<>();
        if (lastCycleEatingPlantEvents != null) {
            for (EatingPlantEvent event : lastCycleEatingPlantEvents) {
                positions.add(new Position(event.herbivoreRow(), event.herbivoreCol()));
                positions.add(new Position(event.plantRow(), event.plantCol()));
            }
        }
        return positions;
    }

    private Set<Position> createReproductionPositionsSet() {
        Set<Position> positions = new HashSet<>();
        if (lastCycleReproductionEvents != null) {
            for (ReproductionEvent event : lastCycleReproductionEvents) {
                int[] posParent1 = MapUtils.findOrganismPosition(island, event.parent1());
                int[] posParent2 = MapUtils.findOrganismPosition(island, event.parent2());

                if (posParent1 != null) positions.add(new Position(posParent1[0], posParent1[1]));
                if (posParent2 != null) positions.add(new Position(posParent2[0], posParent2[1]));
            }
        }
        return positions;
    }

    private Set<Position> createOffspringPositionsSet() {
        Set<Position> positions = new HashSet<>();
        if (lastCycleOffspringEvents != null) {
            for (ReproductionEvent event : lastCycleOffspringEvents) {
                positions.add(new Position(event.offspringRow(), event.offspringCol()));
            }
        }
        return positions;
    }
}