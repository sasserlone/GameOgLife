package com.gameoflife

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Hauptklasse der Spring Boot Anwendung für Conway's Game of Life.
 */
@SpringBootApplication
class GameOfLifeApplication

fun main(args: Array<String>) {
    runApplication<GameOfLifeApplication>(*args)
}
