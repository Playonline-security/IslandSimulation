
# 🌴 IslandSimulation

**IslandSimulation** es una simulación de ecosistemas desarrollada en Java. Representa una isla poblada por distintos organismos (animales y plantas) que interactúan entre sí a lo largo de ciclos: nacen, se alimentan, cazan, se reproducen y mueren. Todo el comportamiento se representa visualmente en un archivo HTML interactivo y colorido.

> ⚙️ **Recomendación**: Se recomienda utilizar el IDE [IntelliJ IDEA](https://www.jetbrains.com/idea/) para ejecutar y explorar cómodamente el proyecto.

---

## 📂 Descripción general

- La simulación genera automáticamente un archivo llamado `island.html`.
- Este archivo se abre automáticamente en el navegador **Google Chrome** una vez se ejecuta el programa.
- En él podrás visualizar:
  - El mapa de la isla con los organismos representados por **emojis**.
  - Eventos destacados con animaciones y colores.
  - Un panel de estadísticas detalladas.
  - Un registro de eventos que ocurren en cada ciclo.

---

## 📋 Requisitos previos

Antes de compilar y ejecutar el proyecto, asegúrate de tener:

- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) o superior
- [Apache Maven 3.6+](https://maven.apache.org/)
- [Google Chrome](https://www.google.com/chrome/) instalado (se abre automáticamente)

---

## 🛠 Instalación y ejecución

1. **Clona el repositorio**:
   ```bash
   git clone https://github.com/tu-usuario/IslandSimulation.git
   cd IslandSimulation
   ```

2. **Compila el proyecto con Maven**:
   ```bash
   mvn clean package
   ```

3. **Ejecuta la simulación**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.codegym.islandsimulation.Main"
   ```

> 🖥️ Esto generará y abrirá automáticamente `island.html` en Google Chrome.

---

## 📸 Ejemplo de visualización

<img width="1744" height="862" alt="Captura de pantalla 2025-08-04 182058" src="https://github.com/user-attachments/assets/9fbb093a-6831-4649-96cb-63294d3e3ced" />

---

## 🔽 Cómo contraer o expandir código en IntelliJ IDEA

Cuando explores el código fuente, es útil contraer bloques largos. Usa los siguientes atajos:

| Acción                  | Atajo (Windows/Linux) | Atajo (macOS)       |
|-------------------------|------------------------|----------------------|
| Contraer bloque actual  | `Ctrl + -`             | `Cmd + -`            |
| Expandir bloque actual  | `Ctrl + +`             | `Cmd + +`            |
| Contraer todos los bloques | `Ctrl + Shift + -`    | `Cmd + Shift + -`    |
| Expandir todos los bloques | `Ctrl + Shift + +`    | `Cmd + Shift + +`    |

---

## 🧪 Áreas de mejora

Aunque el proyecto ya es funcional y visualmente atractivo, existen oportunidades para seguir creciendo y mejorando:

- 🔁 Optimizar el manejo de **eventos y logs** para mejorar rendimiento en simulaciones a gran escala.
- 🧪 Implementar **pruebas unitarias** para asegurar la estabilidad de funciones clave.
- 🎨 Modularizar aún más el código **HTML y CSS** para facilitar futuras personalizaciones visuales.
- ⏱️ Agregar **control del tiempo** entre ciclos para visualizaciones más pausadas o en tiempo real.
- 💾 Guardar el estado de la simulación en archivos para permitir continuidad o análisis posterior.
- 📊 Generar gráficos o reportes estadísticos al final de cada simulación.
- 🧠 Incluir una pequeña **IA** para ajustar comportamientos de organismos con base en el entorno.

---

## 🚧 Futuro del proyecto

Este proyecto se seguirá optimizando con el objetivo de:

- Convertirse en una simulación más compleja e interactiva.
- Explorar nuevas técnicas de desarrollo con Java.
- Aprender sobre concurrencia, estructuras de datos, visualización y buenas prácticas de arquitectura.

Cada mejora representa un paso más en el camino de aprendizaje hacia el desarrollo profesional en Java.

---

## 👨‍💻 Créditos

Desarrollado por:

**Jonathan Uzcátegui González**  
Estudiante de Ingeniería de Software  
📚 Corporación Universitaria Iberoamericana  
🔐 Futuro ethical hacker y desarrollador Java
