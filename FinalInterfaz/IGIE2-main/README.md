# IE - Interfaz Gráfica

## 📋 Descripción del Proyecto

Este proyecto consiste en una mejora del sistema de batalla desarrollado durante la 2° Instancia Evaluativa.
Se implementó una aplicación en Java con interfaz gráfica Swing, utilizando arquitectura MVC e integrando una base de datos SQLite mediante JDBC para persistencia de datos.

El sistema permite simular batallas entre personajes tipo Héroe y Villano, registrar los resultados en una base de datos y consultar estadísticas y rankings desde la interfaz gráfica.

**Objetivo del Proyecto:**
El objetivo del proyecto es desarrollar una aplicación de simulación de batallas entre personajes tipo Héroe y Villano, utilizando Java con interfaz gráfica Swing, aplicando arquitectura MVC (Modelo-Vista-Controlador) e integrando una base de datos SQLite mediante JDBC para la persistencia de datos.

El sistema permite configurar personajes, ejecutar batallas automáticas y visualizar en tiempo real el estado de los personajes durante el combate. Además, los resultados de las batallas se almacenan en la base de datos para poder consultar estadísticas, ranking de personajes e historial de combates.

Como mejoras adicionales al proyecto base se implementaron funcionalidades extra como Sistema de Logros (Achievements) y Modo Torneo, que permiten ampliar la experiencia del sistema y cumplir con los requisitos del examen final.

**Video Explicativo:**




## 👥 Integrantes del Equipo


- **[Mateo Paredes]**- PROMPTS (https://chatgpt.com/share/69adc85d-a338-8002-9488-e026ddfad4b8)
Durante el desarrollo se utilizó ChatGPT como herramienta de asistencia para depuración de código, consultas SQL, implementación de JDBC y revisión de arquitectura MVC.


## 🔨 Funcionalidades Implementadas

Reestructuración del proyecto utilizando arquitectura MVC.

Ventana de Configuración Inicial
Permite registrar personajes, asignar características iniciales y validar configuraciones antes de comenzar la batalla.

Ventana Principal de Batalla
Muestra información dinámica del combate, incluyendo vida de los personajes, armas invocadas y eventos de batalla.

Sistema de Combate por Turnos
Los personajes pueden invocar armas, realizar ataques y utilizar habilidades especiales.

Persistencia con Base de Datos (SQLite)
Se registran en la base de datos:

personajes

resultados de batallas

estadísticas acumuladas

Ranking de Personajes
Muestra los personajes ordenados según la cantidad de victorias.

Historial de Batallas
Consulta las últimas batallas registradas en la base de datos.

⭐ Funcionalidades Extra

Para obtener una nota superior a 7 se implementaron funcionalidades adicionales.

Sistema de Logros (Achievements)

Se desbloquean logros según condiciones específicas durante la batalla.

Ejemplos:

Maestro de batalla → ganar en menos de 15 turnos

Invocador → invocar múltiples armas

Dominador → ganar con más de 100 puntos de vida

Modo Torneo

Permite ejecutar múltiples batallas consecutivas entre personajes.

Características:

Simulación de varias batallas seguidas

Registro de victorias de héroe y villano

Determinación de un ganador final del torneo

## 📁 Estructura del Proyecto

```
IEIG2
│
├── src
│   └── ieig2
│
│       ├── modelo
│       │   ├── Personaje.java
│       │   ├── Heroe.java
│       │   ├── Villano.java
│       │   ├── Arma.java
│       │   ├── HistorialBatallas.java
│       │   ├── PersonajeDAO.java
│       │   ├── BatallaDAO.java
│       │   └── ConexionDB.java
│       │
│       ├── vista
│       │   ├── BatallaVista.java
│       │   ├── ConfigInicialPanel.java
│       │   └── VentanaReporteFinal.java
│       │
│       └── controlador
│           ├── GameController.java
│           └── ConfigInicialControlador.java
│
├── database.sql
└── README.md

```

## 🚀 Instalación y Uso

EN EL VIDEO SE INFORMA COMO UTILIZAR. 3513101109 MATEO PAREDES
---

*Proyecto desarrollado para la materia Interfaz Grafica*
