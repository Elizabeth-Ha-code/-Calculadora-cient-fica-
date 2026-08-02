# Calculadora Científica en Java

Aplicación de escritorio desarrollada en **Java (Swing)** que implementa una calculadora científica completa, ideal como proyecto de portafolio.

##  Características

- Operaciones básicas: suma, resta, multiplicación y  división.
- Funciones trigonométricas: `sin`, `cos`, `tan` (con soporte para grados/radianes)
- Funciones logarítmicas: `log` (base 10) y `ln` (natural)
- Potencias y raíces: `x²`, `x^y`, `√`
- Constantes matemáticas: `π` y `e`
- Uso de paréntesis y respeto de precedencia de operadores
- Motor de evaluación propio basado en el algoritmo **shunting-yard** (notación infija → postfija)
- Interfaz gráfica simple e intuitiva con Java Swing

## Requisitos

- JDK 11 o superior


## Tecnologías

- **Lenguaje:** Java
- **GUI:** Java Swing

## Estructura

```
CalculadoraCientifica.java   # Código fuente completo (GUI + lógica + motor de evaluación)
README.md                    # Este archivo
```

## Posibles mejoras futuras

- Historial de operaciones
- Modo de calculadora de programador (binario, hexadecimal)
- Soporte para variables (memoria M+, M-, MR)
- Tests unitarios con JUnit
