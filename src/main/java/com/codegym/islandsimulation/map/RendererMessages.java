package com.codegym.islandsimulation.map;

import java.util.HashMap;
import java.util.Map;
/**
 * Clase que maneja los mensajes de la interfaz de usuario según el idioma.
 * Proporciona mensajes en inglés o español basados en el parámetro de idioma.
 */
public class RendererMessages {
    private final Map<String, String> messages;
    /**
     * Constructor que inicializa los mensajes según el idioma.
     *
     * @param language Idioma para los mensajes ("en" para inglés, cualquier otro para español).
     */
    public RendererMessages(String language) {
        messages = new HashMap<>();
        if ("en".equalsIgnoreCase(language)) {
            // Inglés
            messages.put("title", "Island Simulation");
            messages.put("mapTitle", "🌍 Island Map");
            messages.put("stats", "📊 Statistics");
            messages.put("generalStatus", "General Status");
            messages.put("cycle", "Cycle");
            messages.put("totalPopulation", "Total Population");
            messages.put("populationBySpecies", "Population by Species");
            messages.put("log", "Consolidated Log");
            messages.put("critical", "Critical");
            messages.put("low", "Low");
            messages.put("stable", "Stable");
        } else {
            // Español por defecto
            messages.put("title", "Isla Simulación");
            messages.put("mapTitle", "🌍 Mapa de la Isla");
            messages.put("stats", "📊 Estadísticas");
            messages.put("generalStatus", "Estado General");
            messages.put("cycle", "Ciclo");
            messages.put("totalPopulation", "Población Total");
            messages.put("populationBySpecies", "Población por Especie");
            messages.put("log", "Log Consolidado");
            messages.put("critical", "Crítica");
            messages.put("low", "Baja");
            messages.put("stable", "Estable");
        }
    }
    /**
     * Obtiene el mensaje correspondiente a la clave proporcionada.
     *
     * @param key Clave del mensaje.
     * @return Mensaje en el idioma seleccionado, o la clave si no se encuentra el mensaje.
     */
    public String get(String key) {
        return messages.getOrDefault(key, key);
    }
}