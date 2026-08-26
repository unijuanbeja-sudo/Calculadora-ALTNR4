# Calculadora Científica con ANTLR

## Lenguajes de Programación y Traducción

Este proyecto consiste en el desarrollo progresivo de una calculadora científica utilizando **ANTLR** y el patrón de diseño **Visitor**.

El objetivo es construir un pequeño lenguaje matemático capaz de interpretar expresiones, almacenar variables, ejecutar funciones matemáticas y, en etapas posteriores, representar funciones gráficamente.

La arquitectura general utilizada por el intérprete es:

```text
Entrada del usuario
        ↓
      Lexer
        ↓
      Tokens
        ↓
      Parser
        ↓
Árbol sintáctico
        ↓
     Visitor
        ↓
    Resultado

## 1. Estructura inicial del proyecto

Se creó la carpeta principal del proyecto con los archivos base necesarios para desarrollar la calculadora científica con ANTLR.

La estructura inicial es:

```text
CalculadoraCientifica/
├── ScientificCalc.g4
├── Main.java
├── ScientificEvalVisitor.java
├── PlotWindow.java
├── ejemplos.txt
└── README.md

<img width="660" height="206" alt="image" src="https://github.com/user-attachments/assets/3248df74-77b8-40ed-81a8-4fcc58d3daf8" />


