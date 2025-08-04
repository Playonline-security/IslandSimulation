package com.codegym.islandsimulation;

import com.codegym.islandsimulation.engine.IslandEngine;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("🚀 Iniciando la simulación...");
        // Inicializar el motor de simulación con 20 islas y 50 ciclos
        IslandEngine engine = new IslandEngine(20, 50);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 Cerrando simulación de forma ordenada...");
            engine.shutdown();
        }));

        System.out.println("Ciclo " + engine.getCycleNumber() + " en ejecución.");

        openInBrowser();

        while (true) {
            engine.updateSimulationCycle();
            System.out.println("Ciclo " + engine.getCycleNumber() + " en ejecución.");

            Thread.sleep(3000);
        }
    }

    private static void openInBrowser() throws IOException {
        File htmlFile = new File("island.html");

        for (int i = 0; i < 5; i++) {
            if (htmlFile.exists()) {
                System.out.println("✅ Abriendo archivo en el navegador: " + htmlFile.getAbsolutePath());
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(htmlFile.toURI());
                } else {
                    System.out.println("🌐 Desktop no soportado, usando cmd...");
                    new ProcessBuilder("cmd", "/c", "start", "chrome", htmlFile.getAbsolutePath()).start();
                }
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.err.println("⚠️ El archivo HTML no fue encontrado después de varios intentos. Intente abrir 'island.html' manualmente.");
    }
}